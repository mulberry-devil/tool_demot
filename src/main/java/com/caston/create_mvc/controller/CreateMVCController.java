package com.caston.create_mvc.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.caston.create_mvc.entity.SQLEntity;
import com.caston.create_mvc.service.CreateMVCService;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/createMVC")
@RequiresRoles(value = {"manager"}, logical = Logical.OR)
public class CreateMVCController {

    @Resource
    private SQLEntity sqlEntity;
    @Resource
    private DruidDataSource dataSource;
    @Resource
    private CreateMVCService createMVCService;

    @PostMapping("/")
    public void create(@RequestPart MultipartFile file, String parent, String author, HttpServletResponse response) throws Exception {
        // 解析sql文件获得表名列表
        List<String> sqlNames = createMVCService.parseSQL(file);
        // 执行sql文件
        DruidPooledConnection connection = dataSource.getConnection();
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.runScript(new BufferedReader(new InputStreamReader(file.getInputStream())));
        // 生成压缩包文件并提供下载
        createMVCService.genZip(author, parent, sqlNames, response);
        // 删除表
        createMVCService.dropTable(sqlNames);
    }
}
