package com.caston.netdisc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

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
 * @since 2022-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("fileName")
    private String filename;

    @TableField("fileUrl")
    private String fileurl;


}
