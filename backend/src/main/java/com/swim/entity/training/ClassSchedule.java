package com.swim.entity.training;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("class_schedule")
public class ClassSchedule extends BaseEntity {

    private String scheduleNo;

    private Long classId;

    private Long courseId;

    private Long coachId;

    private Long venueId;

    private LocalDate classDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal classHours;

    private Integer scheduleStatus;

    private Integer isMakeup;

    private Long originalScheduleId;

    private java.time.LocalDateTime actualStartTime;

    private java.time.LocalDateTime actualEndTime;

    private Integer attendanceCount;

    private Integer absentCount;

    private Integer leaveCount;

    private String teachingContent;

    private String coachRemark;
}
