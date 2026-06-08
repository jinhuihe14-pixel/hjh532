package com.swim.entity.schedule;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("schedule_plan")
public class SchedulePlan extends BaseEntity {

    private String planNo;

    private String planName;

    private String positionType;

    private String scheduleType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalShifts;

    private Integer totalStaff;

    private Integer planStatus;

    private LocalDateTime publishTime;

    private Long publisherId;

    private String publisherName;

    private String remark;
}
