package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.io.Serializable;


/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_audit")
public class CourseAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 审核意见
     */
    private String auditMind;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 审核人
     */
    private String auditPeople;

    /**
     * 审核时间
     */
    private LocalDateTime auditDate;


}
