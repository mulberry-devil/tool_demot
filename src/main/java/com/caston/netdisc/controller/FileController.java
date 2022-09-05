package com.caston.netdisc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.netdisc.entity.File;
import com.caston.netdisc.exception.NetDiscException;
import com.caston.netdisc.service.FileService;
import com.caston.netdisc.utils.FastDFSUtil;
import com.caston.send_mail.mq.handler.MailHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author caston
 * @since 2022-09-02
 */
@RestController
@RequestMapping("/file")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public void upload(@RequestPart MultipartFile[] files) {
        for (MultipartFile file : files) {
            try {
                log.info("开始上传{}到fastdfs...", file);
                String path = fastDFSUtil.uploadFile(file);
                String viewAccessUrl = fastDFSUtil.getViewAccessUrl(path);
                String filename = file.getOriginalFilename();
                File oldFile = fileService.getOne(new LambdaQueryWrapper<File>().eq(File::getFilename, filename));
                if (oldFile != null) {
                    fastDFSUtil.deleteFile(oldFile.getFileurl());
                    oldFile.setFileurl(viewAccessUrl);
                    fileService.updateById(oldFile);
                } else {
                    fileService.save(new File(filename, viewAccessUrl));
                }
                log.info("{}上传成功，访问路径为：{}", filename, viewAccessUrl);
            } catch (Exception e) {
                log.error("{}上传错误：", file, e);
            }
        }
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam String... fileNames) {
        for (String fileName : fileNames) {
            try {
                log.info("开始删除{}", fileName);
                LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<File>().eq(File::getFilename, fileName);
                File file = fileService.getOne(queryWrapper);
                if (file == null) {
                    log.info("没有{}这个文件", fileName);
                    throw new NetDiscException("没有" + fileName + "该文件");
                }
                fileService.remove(queryWrapper);
                fastDFSUtil.deleteFile(file.getFileurl());
                log.info("{}删除成功", fileName);
            } catch (Exception e) {
                log.error("{}删除失败", fileName, e);
            }
        }
    }

    @GetMapping("/downloadBatch")
    public void downloadBatch(HttpServletResponse response, String... fileNames) {
        List<File> list = new ArrayList<>();
        for (String fileName : fileNames) {
            File file = fileService.getOne(new LambdaQueryWrapper<File>().eq(File::getFilename, fileName));
            if (file == null) {
                log.info("没有{}这个文件", fileName);
                continue;
            }
            list.add(file);
        }
        InputStream inputStream = fastDFSUtil.downloadFile(list);
        byte[] buffer = new byte[1024];
        BufferedInputStream in = null;
        OutputStream os = null;
        try {
            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode("download.zip", "UTF-8"));
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            os = response.getOutputStream();
            in = new BufferedInputStream(inputStream);
            while (in.read(buffer) != -1) {
                os.write(buffer);
            }
            log.info("下载完成");
        } catch (Exception e) {
            log.error("下载错误：", e);
        } finally {
            try {
                // 关闭资源
                if (in != null) {
                    in.close();
                }
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/download")
    public void download(String fileName, HttpServletResponse response) {
        BufferedInputStream in = null;
        OutputStream os = null;
        try {
            log.info("开始下载{}", fileName);
            File file = fileService.getOne(new LambdaQueryWrapper<File>().eq(File::getFilename, fileName));
            if (file == null) {
                log.info("没有{}这个文件", fileName);
                throw new NetDiscException("没有" + fileName + "该文件");
            }
            InputStream download = fastDFSUtil.download(file.getFileurl());
            byte[] buffer = new byte[1024];
            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(file.getFilename(), "UTF-8"));
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            os = response.getOutputStream();
            in = new BufferedInputStream(download);
            while (in.read(buffer) != -1) {
                os.write(buffer);
            }
            log.info("下载{}完成", fileName);
        } catch (Exception e) {
            log.error("{}下载错误：", fileName, e);
        } finally {
            try {
                // 关闭资源
                if (in != null) {
                    in.close();
                }
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

