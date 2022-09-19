package com.caston.wechat.utils;

import com.caston.wechat.entity.RespMessage_Text;
import com.caston.wechat.service.impl.WechatServiceImpl;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeChatUtil {
    private static final Logger log = LoggerFactory.getLogger(WeChatUtil.class);

    public static String wxSignatureSort(String token, String timestamp, String nonce) {
        log.info("[WeChatUtil wxSignatureSort]WeChatUtil wxSignatureSort 执行...");
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder builder = new StringBuilder();
        for (String str : strArray) {
            builder.append(str);
        }
        log.info("[WeChatUtil wxSignatureSort]WeChatUtil wxSignatureSort 执行结束...");
        return builder.toString();
    }

    public static String wxSignatureSHA1(String decrypt) {
        try {
            log.info("[WeChatUtil wxSignatureSHA1]WeChatUtil wxSignatureSHA1 执行...");
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1");
            digest.update(decrypt.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            log.info("[WeChatUtil wxSignatureSHA1]WeChatUtil wxSignatureSHA1 执行结束...");
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("[WeChatUtil wxSignatureSHA1]WeChatUtil wxSignatureSHA1 执行出错：", e);
        }
        return "";
    }

    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    public static Map<String, String> xml2MapFromStream(InputStream inputStream){
        Map<String, String> map = new HashMap<String, String>();
        try {
            log.info("[WeChatUtil xml2MapFromStream]WeChatUtil xml2MapFromStream 开始转换为map...");
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点
            for (Element e : elementList)
                map.put(e.getName(), e.getText());
            log.info("[WeChatUtil xml2MapFromStream]WeChatUtil xml2MapFromStream 转换为map成功...");
        }catch (Exception e){
            log.error("[WeChatUtil xml2MapFromStream]WeChatUtil xml2MapFromStream 执行出错：", e);
        }
        return map;
    }

    public static String messageToXml(RespMessage_Text responseText) {
        xstream.alias("xml", responseText.getClass());
        return xstream.toXML(responseText);
    }
}
