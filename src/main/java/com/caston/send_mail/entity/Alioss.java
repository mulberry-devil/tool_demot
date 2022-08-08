package com.caston.send_mail.entity;

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
 * @since 2022-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Alioss implements Serializable {

    private static final long serialVersionUID = 1L;

    private String endpoint;

    @TableField("accessKeyId")
    private String accesskeyid;

    @TableField("accessKeySecret")
    private String accesskeysecret;


}
