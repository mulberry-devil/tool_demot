package com.caston.wechat.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author caston
 * @since 2022-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class WechatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("fromUserName")
    private String fromusername;

    private String content;

    @TableField("createTime")
    private String createtime;

}
