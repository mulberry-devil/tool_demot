package com.caston;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.caston.wechat.entity.Content;
import com.caston.wechat.entity.MessageMap;
import com.caston.wechat.entity.WechatNote;
import com.caston.wechat.entity.WechatToken;
import com.caston.wechat.enums.WeChatEnum;
import com.caston.wechat.service.WechatTokenService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

// @SpringBootTest
class ToolDemoApplicationTests {

    @Test
    void contextLoads() {
        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();
// 2、全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("caston");
        gc.setOpen(false); //生成后是否打开资源管理器
        gc.setServiceName("%sService");    //去掉Service接口的首字母I
        mpg.setGlobalConfig(gc);
// 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://175.178.70.91:3306/tool?serverTimezone=UTC&characterEncoding=utf-8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("tool_root");
        dsc.setPassword("ysn2jKccjyL4yYnH");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);
// 4、包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.caston.wechat");
        pc.setEntity("entity"); //此对象与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
        mpg.setPackageInfo(pc);
// 5、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude(new String[]{"wechat_message"});//指定表
        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok
        strategy.setRestControllerStyle(true); //restful api风格控制器
        mpg.setStrategy(strategy);
// 6、执行
        mpg.execute();
    }

    @Test
    void test1() {
        String apiPath = "https://tenapi.cn/resou/";
        BufferedReader in = null;
        StringBuffer result = null;
        try {
            URL url = new URL(apiPath);
            //打开和url之间的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            connection.connect();
            result = new StringBuffer();
            //读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            String result2 = result.toString(); //返回json字符串
            //获取数据
            JSONObject jsonObject = JSON.parseObject(result2);
            System.out.println(jsonObject);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    void test2() {
        String password = "123123";
        Md5Hash md5Hash = new Md5Hash(password);
        System.out.println(md5Hash.toHex());
        // 使用加盐后的加密数据
        Md5Hash md5Hash1 = new Md5Hash(password, "bjsxd");
        System.out.println(md5Hash1.toHex());
        // 使用迭代并加盐后的加密数据
        Md5Hash md5Hash2 = new Md5Hash(password, "bjsxd", 2);
        System.out.println(md5Hash2.toHex());
    }

    @Test
    void test3() {
        // ALiOSSEnum.ENDPOINT.setAliField("aaaaa");
        // ALiOSSEnum.ACCESSKEYID.setAliField("bbbbb");
        // ALiOSSEnum.ACCESSKEYSECRET.setAliField("ccccc");
        // System.out.println(ALiOSSEnum.ENDPOINT.getAliField());
        // Map<String, String> build = new MessageMap.Builder().put("aaa", "aaa").put("bbb", "bbb").build();
        // System.out.println(build);
        //
        // Map<String, String> build1 = new MessageMap.Builder().put("ccc", "ccc").put("ddd", "ddd").build();
        // System.out.println(build1);
        MessageMap.Builder builder = new MessageMap.Builder();
        Map<String, Content> build = builder.put("aaaa", "aaa").put("bbb", "bbbb").build();
        Map<String, Content> build1 = builder.put("ccc", "ccc").put("ddd", "dddd").build();
        System.out.println();
    }

    @Autowired
    private WechatTokenService wechatTokenService;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void test4() {
        WechatToken wechatToken = wechatTokenService.getOne(null);
        String access_token = "";
        if (wechatToken == null) {
            String url = WeChatEnum.ACCESS_TOKEN_URL.getAliField().replace("APPID", WeChatEnum.APPID.getAliField()).replace("APPSECRET", WeChatEnum.APPSECRET.getAliField());
            ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
            JSONObject jsonObject = JSONObject.parseObject(forEntity.getBody());
            Object errcode = jsonObject.get("errcode");
            if (errcode != null && "40013".equals(errcode.toString())) {
                return;
            }
            access_token = jsonObject.getString("access_token");
            int expires_in = jsonObject.getIntValue("expires_in");
            WechatToken token = new WechatToken(access_token, new Date(), expires_in);
            wechatTokenService.save(token);
        } else {
            Date now = new Date();
            Date expire = new Date(wechatToken.getStartTime().getTime() + wechatToken.getExpiresIn() * 1000);
            if (now.compareTo(expire) != -1) {
                String url = WeChatEnum.ACCESS_TOKEN_URL.getAliField().replace("APPID", WeChatEnum.APPID.getAliField()).replace("APPSECRET", WeChatEnum.APPSECRET.getAliField());
                ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
                JSONObject jsonObject = JSONObject.parseObject(forEntity.getBody());
                Object errcode = jsonObject.get("errcode");
                if (errcode != null && "40013".equals(errcode.toString())) {
                    return;
                }
                access_token = jsonObject.getString("access_token");
                int expires_in = jsonObject.getIntValue("expires_in");
                WechatToken token = new WechatToken(access_token, new Date(), expires_in);
                wechatTokenService.update(token, null);
            } else {
                access_token = wechatToken.getAccessToken();
            }
        }

//        String city_url = "https://geoapi.qweather.com/v2/city/lookup?key=5d3cb817dd424f98a1f92c0173283102&&location=常熟";
//        ResponseEntity<String> city = restTemplate.getForEntity(city_url, String.class);
        String url_weather = "https://tenapi.cn/wether/?city=常熟";
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        String response = restTemplate.exchange(url_weather, HttpMethod.GET, entity, String.class).getBody();
        JSONObject json = JSONObject.parseObject(response);
        JSONObject data = json.getJSONObject("data").getJSONArray("forecast").getJSONObject(0);
        String date = data.getString("date");
        String high = data.getString("high");
        String low = data.getString("low");
        String fengxiang = data.getString("fengxiang");
        String type = data.getString("type");
        String wendu = json.getJSONObject("data").getString("wendu");

        String templateId = "lRWtq-7TyHay1YmrRRwnyaqIEJ02aeGaALJ6TPaoyHs";
        Map<String, Content> sendMag = new HashMap<>();
        DateFormat formatter = DateFormat.getDateTimeInstance();
        sendMag.put("date", new Content(formatter.format(new Date()), "#f6bec8"));
        sendMag.put("city", new Content("常熟", "#a85858"));
        sendMag.put("wether", new Content(type));
        sendMag.put("current", new Content(wendu));
        sendMag.put("high", new Content(high));
        sendMag.put("low", new Content(low));
        sendMag.put("fengxiang", new Content(fengxiang));
        sendMag.put("day", new Content("已经做了5000天地球人"));
        sendMag.put("note", new Content("明天要把冰箱里的酸奶带到公司\n测试换行"));
        String url = WeChatEnum.SEND_URL.getAliField().replace("ACCESS_TOKEN", access_token);
        Map<String, Object> sendBody = new HashMap<>();
        sendBody.put("touser", "oQ4gy6YwpA2o117Vx5dzy0WQ0rCY");                 // openId
        sendBody.put("topcolor", "#FF0000");          // 顶色
        sendBody.put("data", sendMag);                   // 模板参数
        sendBody.put("template_id", templateId);      // 模板Id
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, sendBody, String.class);
        JSONObject jsonObject = JSONObject.parseObject(forEntity.getBody());
        System.out.println(jsonObject);
    }

    @Test
    void test5() {
        Map<String, Content> sendMag = new HashMap<>();
        String city_url = WeChatEnum.CITY_URL.getAliField().replace("REGION", "常熟");
        ResponseEntity<String> city = restTemplate.getForEntity(city_url, String.class);
        JSONObject city_json = JSONObject.parseObject(city.getBody());
        JSONArray location = city_json.getJSONArray("location");
        location.forEach(i -> {
            JSONObject region_json = (JSONObject) i;
            String adm2 = region_json.getString("adm2");
            if (adm2.contains("苏州")) {
                String id = region_json.getString("id");
                String weather_url = WeChatEnum.WEATHER_URL.getAliField().replace("TYPE", "3d").replace("CITYID", id);
                ResponseEntity<String> weather = restTemplate.getForEntity(weather_url, String.class);
                JSONObject weather_json = JSONObject.parseObject(weather.getBody());
                JSONObject daily = weather_json.getJSONArray("daily").getJSONObject(0);
                String fxDate = daily.getString("fxDate");
                String textDay = daily.getString("textDay");
                String textNight = daily.getString("textNight");
                String tempMax = daily.getString("tempMax");
                String tempMin = daily.getString("tempMin");
                String windDirDay = daily.getString("windDirDay");
                String windScaleDay = daily.getString("windScaleDay");
                String windSpeedDay = daily.getString("windSpeedDay");
                String windDirNight = daily.getString("windDirNight");
                String windScaleNight = daily.getString("windScaleNight");
                String windSpeedNight = daily.getString("windSpeedNight");
                String uvIndex = daily.getString("uvIndex");
                String vis = daily.getString("vis");
                String weather_now_url = WeChatEnum.WEATHER_URL.getAliField().replace("TYPE", "now").replace("CITYID", id);
                ResponseEntity<String> weather_now = restTemplate.getForEntity(weather_now_url, String.class);
                JSONObject weather_now_json = JSONObject.parseObject(weather_now.getBody());
                String temp = weather_now_json.getJSONObject("now").getString("temp");
                String text_url = WeChatEnum.TEXT_URL.getAliField().replace("CITYID", id);
                ResponseEntity<String> text = restTemplate.getForEntity(text_url, String.class);
                JSONObject text_json = JSONObject.parseObject(text.getBody());
                StringBuilder stringBuilder = new StringBuilder();
                text_json.getJSONArray("daily").forEach(j -> {
                    JSONObject json = (JSONObject) j;
                    stringBuilder.append(json.getString("text") + "\n");
                });
                sendMag.put("fxDate", new Content(fxDate));
                sendMag.put("region", new Content("常熟"));
                sendMag.put("textDay", new Content(textDay));
                sendMag.put("textNight", new Content(textNight));
                sendMag.put("temp", new Content(temp));
                sendMag.put("tempMax", new Content(tempMax));
                sendMag.put("tempMin", new Content(tempMin));
                sendMag.put("windDirDay", new Content(windDirDay));
                sendMag.put("windScaleDay", new Content(windScaleDay));
                sendMag.put("windSpeedDay", new Content(windSpeedDay));
                sendMag.put("windDirNight", new Content(windDirNight));
                sendMag.put("windScaleNight", new Content(windScaleNight));
                sendMag.put("windSpeedNight", new Content(windSpeedNight));
                sendMag.put("uvIndex", new Content(uvIndex));
                sendMag.put("vis", new Content(vis));
                sendMag.put("day", new Content("5000"));
                sendMag.put("note", new Content(stringBuilder.toString()));
            }
        });
    }

    @Test
    void test6() {
        String city_url = WeChatEnum.CITY_URL.getAliField().replace("REGION", "从化");
        ResponseEntity<String> city = restTemplate.getForEntity(city_url, String.class);
        JSONObject city_json = JSONObject.parseObject(city.getBody());
        JSONArray location = city_json.getJSONArray("location");
        String id = location.getJSONObject(0).getString("id");
        String weather_url = WeChatEnum.WEATHER_URL.getAliField().replace("TYPE", "3d").replace("CITYID", id);
        ResponseEntity<String> weather = restTemplate.getForEntity(weather_url, String.class);
        JSONObject weather_json = JSONObject.parseObject(weather.getBody());
        String weather_now_url = WeChatEnum.WEATHER_URL.getAliField().replace("TYPE", "now").replace("CITYID", id);
        ResponseEntity<String> weather_now = restTemplate.getForEntity(weather_now_url, String.class);
        JSONObject weather_now_json = JSONObject.parseObject(weather_now.getBody());
        String text_url = WeChatEnum.TEXT_URL.getAliField().replace("CITYID", id);
        ResponseEntity<String> text = restTemplate.getForEntity(text_url, String.class);
        JSONObject text_json = JSONObject.parseObject(text.getBody());
        JSONObject daily = weather_json.getJSONArray("daily").getJSONObject(0);
        String fxDate = daily.getString("fxDate");
        String textDay = daily.getString("textDay");
        String textNight = daily.getString("textNight");
        String tempMax = daily.getString("tempMax");
        String tempMin = daily.getString("tempMin");
        String windDirDay = daily.getString("windDirDay");
        String windScaleDay = daily.getString("windScaleDay");
        String windSpeedDay = daily.getString("windSpeedDay");
        String windDirNight = daily.getString("windDirNight");
        String windScaleNight = daily.getString("windScaleNight");
        String windSpeedNight = daily.getString("windSpeedNight");
        String uvIndex = daily.getString("uvIndex");
        String vis = daily.getString("vis");
        String temp = weather_now_json.getJSONObject("now").getString("temp");
        StringBuilder stringBuilder = new StringBuilder();
        JSONArray text_total = text_json.getJSONArray("daily");
        int k = 1;
        for (Object j : text_total) {
            JSONObject json = (JSONObject) j;
            stringBuilder.append(k++ + ". " + json.getString("text") + "\n");
        }
        System.out.println();
    }

    @Test
    void test7(){
        Timestamp timestamp = Timestamp.valueOf("1348831860");
        System.out.println(timestamp.getTime());
    }
}

