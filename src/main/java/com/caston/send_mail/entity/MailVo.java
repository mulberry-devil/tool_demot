package com.caston.send_mail.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailVo implements Serializable {
    private String to;
    private String cc;
    private String subject;
    private String text;
    private Boolean isHtml;
    private String filesStr;
}
