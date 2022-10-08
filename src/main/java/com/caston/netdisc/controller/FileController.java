package com.caston.netdisc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.common.result.Response;
import com.caston.netdisc.entity.File;
import com.caston.netdisc.exception.NetDiscException;
import com.caston.netdisc.service.FileService;
import com.caston.netdisc.utils.FastDFSUtil;
import com.caston.send_mail.mq.handler.MailHandler;
import com.caston.shiro.entity.Account;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
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
@RequiresRoles(value = {"manager", "user"}, logical = Logical.OR)
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Response upload(@RequestPart MultipartFile[] files) {
        List<String> list = new ArrayList<>();
        Account account = (Account) SecurityUtils.getSubject().getPrincipal();
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
                    fileService.save(new File(filename, viewAccessUrl, account.getUsername()));
                }
                log.info("{}上传成功，访问路径为：{}", filename, viewAccessUrl);
            } catch (Exception e) {
                list.add(file.getOriginalFilename());
                log.error("{}上传错误：", file, e);
            }
        }
        if (list.size() == files.length) {
            return Response.error().message("文件上传失败");
        } else if (list.size() != 0) {
            return Response.success(list).message("有文件上传失败");
        }
        return Response.success();
    }

    @DeleteMapping("/delete")
    public Response delete(@RequestParam String... fileNames) {
        List<String> list = new ArrayList<>();
        Account account = (Account) SecurityUtils.getSubject().getPrincipal();
        for (String fileName : fileNames) {
            try {
                log.info("开始删除{}", fileName);
                LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<File>().eq(File::getFilename, fileName).eq(File::getUploaduser, account.getUsername());
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
        if (list.size() == fileNames.length) {
            return Response.error().message("文件删除失败");
        } else if (list.size() != 0) {
            StringBuilder builder = new StringBuilder();
            list.forEach(i -> builder.append(i + ";"));
            return Response.success(list).message("有文件删除失败");
        }
        return Response.success();
    }

    @GetMapping("/downloadBatch")
    public Response downloadBatch(HttpServletResponse response, String... fileNames) {
        List<File> list = new ArrayList<>();
        Account account = (Account) SecurityUtils.getSubject().getPrincipal();
        for (String fileName : fileNames) {
            File file = fileService.getOne(new LambdaQueryWrapper<File>().eq(File::getFilename, fileName).eq(File::getUploaduser, account.getUsername()));
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
            return Response.success().message("批量下载成功");
        } catch (Exception e) {
            log.error("下载错误：", e);
            return Response.error();
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
    public Response download(String fileName, HttpServletResponse response) {
        BufferedInputStream in = null;
        OutputStream os = null;
        Account account = (Account) SecurityUtils.getSubject().getPrincipal();
        try {
            log.info("开始下载{}", fileName);
            File file = fileService.getOne(new LambdaQueryWrapper<File>().eq(File::getFilename, fileName).eq(File::getUploaduser, account.getUsername()));
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
            return Response.success().message("文件下载成功");
        } catch (Exception e) {
            log.error("{}下载错误：", fileName, e);
            return Response.error().message("文件下载失败");
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

    @GetMapping("/getFile")
    public Response getFile() {
        Account account = (Account) SecurityUtils.getSubject().getPrincipal();
        List<File> list = fileService.list(new LambdaQueryWrapper<File>().eq(File::getUploaduser, account.getUsername()));
        return Response.success(list);
    }
}

