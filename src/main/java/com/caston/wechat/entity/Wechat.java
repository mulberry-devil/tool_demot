package com.caston.wechat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
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
public class Wechat implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("appID")
    private String appid;

    private String appsecret;

    private Integer status;

    private String type;
}
