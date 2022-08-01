package com.caston.create_mvc.controller;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.caston.create_mvc.entity.SQLEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/createMVC")
public class CreateMVCController {

    @Resource
    private SQLEntity sqlEntity;

    @PostMapping("/")
    public String create(@RequestParam(required = false) String ipPort,
                         @RequestParam(required = false) String DBName,
                         @RequestParam(required = false) String username,
                         @RequestParam(required = false) String password,
                         @RequestParam(required = false) String projectPath,
                         @RequestParam(required = false) String parent,
                         @RequestParam Boolean all,
                         String[] tables) {
        if (StringUtils.isNoneBlank(ipPort) && StringUtils.isNoneBlank(DBName) && StringUtils.isNoneBlank(username) && StringUtils.isNoneBlank(password)) {
            String url = "jdbc:mysql://" + ipPort + "/" + DBName + "?serverTimezone=UTC&characterEncoding=utf-8";
            sqlEntity.setUrl(url);
            sqlEntity.setUsername(username);
            sqlEntity.setPassword(password);
        }
        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 2、全局配置
        GlobalConfig gc = new GlobalConfig();
        if (!StringUtils.isNoneBlank(projectPath)) {
            projectPath = System.getProperty("user.dir") + "/src/main/java";
        }
        gc.setOutputDir(projectPath);
        gc.setAuthor("caston");
        gc.setOpen(false); //生成后是否打开资源管理器
        gc.setServiceName("%sService");    //去掉Service接口的首字母I
        mpg.setGlobalConfig(gc);
        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(sqlEntity.getUrl());
        dsc.setDriverName(sqlEntity.getDriverClassName());
        dsc.setUsername(sqlEntity.getUsername());
        dsc.setPassword(sqlEntity.getPassword());
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);
        // 4、包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(parent);
        pc.setEntity("entity"); //此对象与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
        mpg.setPackageInfo(pc);
        // 5、策略配置
        StrategyConfig strategy = new StrategyConfig();
        if (all != true && tables != null && tables.length != 0) {
            strategy.setInclude(tables);//指定表
        }
        // strategy.setInclude(new String[]{"test"});//指定表
        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok
        strategy.setRestControllerStyle(true); //restful api风格控制器
        mpg.setStrategy(strategy);
        // 6、执行
        mpg.execute();
        return "创建成功";
    }
}
