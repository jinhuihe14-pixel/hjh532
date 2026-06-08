package com.swim.entity.group;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("group_class_schedule")
public class GroupClassSchedule extends BaseEntity {

    private String scheduleNo;

    private Long classId;

    private String className;

    private Long customerId;

    private Long coachId;

    private String coachName;

    private Long venueId;

    private String venueName;

    private LocalDate classDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal classHours;

    private Integer planStudentCount;

    private Integer actualStudentCount;

    private Integer scheduleStatus;

    private String teachingContent;

    private String coachRemark;
}
