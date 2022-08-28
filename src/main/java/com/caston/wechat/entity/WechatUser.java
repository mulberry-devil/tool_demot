package com.caston.wechat.entity;

import java.io.Serializable;
import java.util.Date;

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
public class WechatUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private String userId;

    private String city;

    private Date birthday;
}
