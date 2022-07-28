# 工具
## 邮件发送动态指定发件人
从数据库读取发件人信息并注入，修改发件人信息时一并修改
- 导入`resource`中的`sql`
- 运行后访问`http://localhost:8080/swagger-ui/`
- 发送邮件字段
    - `to`：收件人，多个时用逗号隔开
    - `cc`：抄送人，多个时用逗号隔开，可不写
    - `subject`：主题
    - `text`：内容，可为html格式
    - `isHtml`：内容为html时选择true
    - `file`：多附件，可不选