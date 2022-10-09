package com.caston.netdisc.utils;

import cn.hutool.core.util.StrUtil;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Data
@Component
public class FastDFSUtil {
    @Autowired
    private FastFileStorageClient storageClient;

    //缩略图处理
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * MultipartFile类型的文件上传ַ
     *
     * @param file
     * @return fastDfs文件保存路径
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        StorePath path = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);
        return path.getFullPath();
    }

    /**
     * 普通的文件上传
     *
     * @param file
     * @return fastDfs文件保存路径
     * @throws IOException
     */
    public String uploadFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        StorePath path = storageClient.uploadFile(inputStream, file.length(),
                FilenameUtils.getExtension(file.getName()), null);
        return path.getFullPath();
    }

    /**
     * 带输入流形式的文件上传
     *
     * @param is          输入流
     * @param fileSize    文件大小
     * @param fileExtName 文件拓展名
     * @return fastDfs文件保存路径
     */
    public String uploadFileStream(InputStream is, long fileSize, String fileExtName) {
        StorePath path = storageClient.uploadFile(is, fileSize, fileExtName, null);
        return path.getFullPath();
    }

    /**
     * 文件上传
     *
     * @param bytes       文件字节
     * @param fileSize    文件大小
     * @param fileExtName 文件扩展名
     * @return fastDfs文件保存路径
     */
    public String uploadFile(byte[] bytes, long fileSize, String fileExtName) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        StorePath storePath = storageClient.uploadFile(byteArrayInputStream, fileSize, fileExtName, null);
        return storePath.getFullPath();
    }

    /**
     * 将一段文本文件写到fastdfs的服务器上
     *
     * @param content     文本内容
     * @param fileExtName 文件扩展名
     * @return fastDfs文件保存路径
     */
    public String uploadFile(String content, String fileExtName) {
        byte[] buff = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream, buff.length, fileExtName, null);
        return storePath.getFullPath();
    }

    /**
     * 上传图片文件和缩略图
     *
     * @param is          输入流
     * @param size        文件大小
     * @param fileExtName 文件扩展名
     * @param metaData    可以为null
     * @return 原始图片地址和缩略图地址
     */
    public String[] upFileImage(InputStream is, long size, String fileExtName, Set<MetaData> metaData) {
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(is, size, fileExtName, metaData);
        String imagePath = storePath.getFullPath();
        String thumbImagePath = thumbImageConfig.getThumbImagePath(imagePath);
        return new String[]{imagePath, thumbImagePath};
    }

    /**
     * 删除文件
     *
     * @param fileUrl
     */
    public void deleteFile(String fileUrl) {
        if (StrUtil.isEmpty(fileUrl)) {
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     *
     * @param fileUrl
     */
    public byte[] downloadFile(String fileUrl) {
        if (StrUtil.isEmpty(fileUrl)) {
            return null;
        }
        StorePath storePath = StorePath.parseFromUrl(fileUrl);
        return storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
    }

    /**
     * 下载文件
     *
     * @param fileUrl
     */
    public InputStream download(String fileUrl) {
        if (StrUtil.isEmpty(fileUrl)) {
            return null;
        }
        StorePath storePath = StorePath.parseFromUrl(fileUrl);
        return storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), (InputStream ins) -> {
            // 将此ins返回给上面的inputStream
            return ins;
        });
    }

    /**
     * 批量下载文件
     *
     * @param fileList
     */
    public InputStream downloadFile(List<com.caston.netdisc.entity.File> fileList) {
        byte[] buffer = new byte[1024];
        // 创建一个新的 byte 数组输出流，它具有指定大小的缓冲区容量
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //创建一个新的缓冲输出流，以将数据写入指定的底层输出流
        BufferedOutputStream fos = new BufferedOutputStream(baos);
        ZipOutputStream zos = new ZipOutputStream(fos);
        try {
            for (com.caston.netdisc.entity.File file : fileList) {
                if (StrUtil.isNotEmpty(file.getFileurl())) {
                    StorePath storePath = StorePath.parseFromUrl(file.getFileurl());
                    byte[] bytes = storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
                    //压缩文件内的文件名称
                    String fileName = file.getFilename();
                    zos.putNextEntry(new ZipEntry(fileName));
                    zos.write(bytes);
                    zos.closeEntry();
                }
            }
            zos.close();
            fos.flush();
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 封装文件通过浏览器直接访问文件的地址
     *
     * @param fileUrl
     * @return
     */
    public String getViewAccessUrl(String fileUrl) {
        return "http://175.178.70.91:8889/" + fileUrl;
    }
}
