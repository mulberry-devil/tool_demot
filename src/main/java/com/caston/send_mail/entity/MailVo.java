package com.caston.send_mail.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author caston
 * @since 2022-08-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MailVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String to;

    private String cc;

    private String subject;

    private String text;

    private Boolean isHtml;

    private String filesStr;

    private LocalDateTime date;
}
