package com.caston.create_mvc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

    public static void createZip(String sourcePath, OutputStream stream) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(stream);
            writeZip(new File(sourcePath), "", zos);
        } catch (Exception e) {
            log.error("[ZipUtil createZip]创建ZIP文件异常", e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                log.error("[ZipUtil createZip]关闭流异常", e);
            }

        }
    }

    private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if (file.exists()) {
            if (file.isDirectory()) {//处理文件夹
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                if (files.length != 0) {
                    for (File f : files) {
                        writeZip(f, parentPath, zos);
                    }
                } else {       //空目录则创建当前目录
                    try {
                        zos.putNextEntry(new ZipEntry(parentPath));
                    } catch (IOException e) {
                        log.error("[ZipUtil writeZip]写入zip异常", e);
                    }
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }

                } catch (Exception e) {
                    log.error("[ZipUtil writeZip]创建ZIP文件异常", e);
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        log.error("[ZipUtil writeZip]关闭流异常", e);
                    }
                }
            }
        }
    }
}
