package com.caston.send_mail.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author caston
 * @since 2022-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SendMail implements Serializable {

    private static final long serialVersionUID = 1L;

    private String host;

    private String username;

    private String password;

    private Integer port;

    private Integer status;


}
