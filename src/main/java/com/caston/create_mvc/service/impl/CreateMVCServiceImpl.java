package com.caston.create_mvc.service.impl;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.caston.create_mvc.mapper.CreateMVCMapper;
import com.caston.create_mvc.service.CreateMVCService;
import com.caston.create_mvc.utils.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CreateMVCServiceImpl implements CreateMVCService {
    @Autowired
    private CreateMVCMapper createMVCMapper;

    private static final Logger log = LoggerFactory.getLogger(CreateMVCServiceImpl.class);

    private static final List<String> DEFAULTTABLE = new ArrayList<>();

    static {
        DEFAULTTABLE.add("account");
        DEFAULTTABLE.add("alioss");
        DEFAULTTABLE.add("file");
        DEFAULTTABLE.add("mail_vo");
        DEFAULTTABLE.add("send_mail");
        DEFAULTTABLE.add("task");
        DEFAULTTABLE.add("task_log");
        DEFAULTTABLE.add("wechat");
        DEFAULTTABLE.add("wechat_message");
        DEFAULTTABLE.add("wechat_note");
        DEFAULTTABLE.add("wechat_template");
        DEFAULTTABLE.add("wechat_token");
        DEFAULTTABLE.add("wechat_user");
    }

    @Override
    public List<String> parseSQL(MultipartFile file) {
        log.info("开始解析sql文件表名...");
        StringBuilder sql = new StringBuilder();
        Reader reader = null;
        BufferedReader br = null;
        String regex = "(?s).*CREATE TABLE.*;";
        Pattern compile = Pattern.compile(regex);
        List<String> sqlNames = new ArrayList<>();
        try {
            reader = new InputStreamReader(file.getInputStream());
            br = new BufferedReader(reader);
            String data;
            while ((data = br.readLine()) != null) {
                sql.append(data + "\n");
                Matcher matcher = compile.matcher(sql.toString());
                if (matcher.find()) {
                    MySqlCreateTableParser parser = new MySqlCreateTableParser(sql.toString());
                    SQLCreateTableStatement sqlCreateTableStatement = parser.parseCreateTable();
                    String tableName = sqlCreateTableStatement.getName().getSimpleName().replace("`", "");
                    log.info("解析到表名：{}", tableName);
                    if (!DEFAULTTABLE.contains(tableName)) sqlNames.add(tableName);
                    sql = new StringBuilder();
                }
            }
        } catch (IOException e) {
            log.error("[CreateMVCServiceImpl parseSQL]解析异常：", e);
        } finally {
            try {
                reader.close();
                br.close();
            } catch (Exception e) {
                log.error("[CreateMVCServiceImpl parseSQL]关闭异常：", e);
            }
        }
        return sqlNames;
    }

    @Override
    public void genZip(String author, String parent, List<String> sqlNames, HttpServletResponse response) {
        try {
            log.info("生成临时文件夹...");
            Path tempDirectory = Files.createTempDirectory(null);
            log.info("临时文件夹为：{}", tempDirectory);
            log.info("开始进行代码生成...");
            // 1、创建代码生成器
            AutoGenerator mpg = new AutoGenerator();
            // 2、全局配置
            GlobalConfig gc = new GlobalConfig();
            gc.setOutputDir(tempDirectory.toString());
            gc.setAuthor(author);
            gc.setOpen(false); //生成后是否打开资源管理器
            gc.setServiceName("%sService");    //去掉Service接口的首字母I
            mpg.setGlobalConfig(gc);
            // 3、数据源配置
            DataSourceConfig dsc = new DataSourceConfig();
            dsc.setUrl("jdbc:mysql://localhost:3306/tool?serverTimezone=UTC&characterEncoding=utf-8");
            dsc.setDriverName("com.mysql.cj.jdbc.Driver");
            dsc.setUsername("tool_root");
            dsc.setPassword("ysn2jKccjyL4yYnH");
            dsc.setDbType(DbType.MYSQL);
            mpg.setDataSource(dsc);
            // 4、包配置
            PackageConfig pc = new PackageConfig();
            pc.setParent("java" + (StringUtils.isNoneBlank(parent) ? "." + parent : ""));
            pc.setEntity("entity"); //此对象与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
            mpg.setPackageInfo(pc);
            // 5、策略配置
            StrategyConfig strategy = new StrategyConfig();
            strategy.setInclude(sqlNames.toArray(new String[sqlNames.size()]));//指定表
            strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
            strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
            strategy.setEntityLombokModel(true); // lombok
            strategy.setRestControllerStyle(true); //restful api风格控制器
            mpg.setStrategy(strategy);
            // 6、执行
            mpg.execute();
            log.info("代码生成完成，开始进行文件夹压缩...");
            response.setContentType("application/octet-stream");
            response.addHeader("content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode("download.zip", "UTF-8"));
            ZipUtil.createZip(tempDirectory + "/java", response.getOutputStream());
            log.info("文件夹压缩完成，开始删除临时文件夹...");
            Files.walkFileTree(tempDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    log.info("文件被删除 : %s%n", file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    log.info("文件夹被删除: %s%n", dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            log.error("[CreateMVCServiceImpl genZip]发生异常：", e);
        }
    }

    @Override
    public void dropTable(List<String> sqlNames) {
        if (sqlNames.size() != 0) {
            log.info("开始进行数据库表删除...");
            StringBuilder dropTable = new StringBuilder("DROP TABLE IF EXISTS");
            for (int i = 0; i < sqlNames.size(); i++) {
                if (i == 0) {
                    dropTable.append(sqlNames.get(i));
                } else {
                    dropTable.append("," + sqlNames.get(i));
                }
            }
            createMVCMapper.excuteSQL(dropTable.toString());
        }
    }
}
