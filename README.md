# 工具
## 邮件发送动态指定发件人
从数据库读取发件人信息并注入，修改发件人信息时一并修改，当发送失败时会进行三次重试，每次间隔5秒，如果还是失败将等待定时任务的处理，定时任务默认每天的23：59进行处理发送失败的所有请求
- 导入`resource`中的`sql`
- 运行后访问`http://localhost:8080/swagger-ui/`
- 发送邮件字段
    - `to`：收件人，多个时用逗号隔开
    - `cc`：抄送人，多个时用逗号隔开，可不写
    - `subject`：主题
    - `text`：内容，可为html格式
    - `isHtml`：内容为html时选择true
    - `file`：多附件，可不选
### 主要使用技术点
- Javamail
- RabbitMQ
- AliOSS
- xxl-job
## 搭建controller、service、mapper架构
默认使用`application.yml`中配置的数据库信息生成所有表，目前只支持本地运行生成
- 运行后访问`http://localhost:8080/swagger-ui/`
- 创建字段
  - `ipPort`：非必填，数据库的IP加端口，例`127.0.0.1:3306`，需要确认能访问到
  - `DBName`：非必填，数据库名
  - `username`：非必填，数据库登录名
  - `password`：非必填，数据库登录密码
  - `projectPath`：指定生成的目录
  - `parent`：指定包的父文件夹
  - `all`：是否生成全部表
  - `tables`：指定表，需要`all`为false

### 主要使用技术点

- MyBatis-plus

## 查询热点数据
支持抖音、微博、知乎、百度、哔哩哔哩热搜
- 运行后访问`http://localhost:8080/swagger-ui/`

### 主要使用技术点

- Redis
- xxl-job
- RestTemplate

## 向公众号推送模板消息

向所有已关注用户推送模板到公众号

- 运行后访问`http://localhost:8080/swagger-ui/`

### 主要使用技术点

- RestTemplate
- xxl-job