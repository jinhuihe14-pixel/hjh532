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
@TableName("group_class")
public class GroupClass extends BaseEntity {

    private String classNo;

    private String className;

    private Long customerId;

    private String customerName;

    private Long groupOrderId;

    private String courseType;

    private Long coachId;

    private String coachName;

    private Long assistantCoachId;

    private Long venueId;

    private String venueName;

    private Integer studentCount;

    private Integer maxStudent;

    private Integer totalSessions;

    private Integer completedSessions;

    private LocalDate classStartDate;

    private LocalDate classEndDate;

    private String classWeekDay;

    private LocalTime classStartTime;

    private LocalTime classEndTime;

    private BigDecimal totalAmount;

    private Integer classStatus;

    private String requirement;

    private String remark;
}
