package com.caston.wechat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class WechatNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("userId")
    private String userid;

    private String note;

    private Date noteDate;

    @TableField("isNew")
    private Integer isnew;
}
