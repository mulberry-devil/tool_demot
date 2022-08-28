package com.caston.wechat.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author caston
 * @since 2022-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class WechatToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accessToken;

    private Date startTime;

    private Integer expiresIn;


}
