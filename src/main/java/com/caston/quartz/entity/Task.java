package com.caston.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 定时任务信息表
 * </p>
 *
 * @author caston
 * @since 2022-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务描述
     */
    private String remaks;

    /**
     * 任务表达式
     */
    private String cron;

    /**
     * 状态 0.正常 1.暂停
     */
    private Integer status;

    /**
     * 任务执行时调用哪个类的方法 包名+类名+方法名
     */
    private String beanClass;

    /**
     * 是否已删除
     */
    private Boolean isDeleted;

    /**
     * 创建者
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 修改者
     */
    private Long updatedBy;

    /**
     * 修改时间
     */
    private Date updatedTime;


}
