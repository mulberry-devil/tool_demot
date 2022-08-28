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
public class WechatTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("templateId")
    private String templateid;

    @TableField("templateName")
    private String templatename;

    private Integer statue;


}
