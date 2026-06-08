package com.swim.entity.schedule;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("schedule_rule")
public class ScheduleRule extends BaseEntity {

    private String ruleName;

    private String positionType;

    private Integer minDailyStaff;

    private Integer maxConsecutiveDays;

    private BigDecimal weeklyRestDays;

    private BigDecimal maxMonthlyHours;

    private Integer shiftRotationEnabled;

    private Integer rotationCycle;

    private Integer weekendStaffBoost;

    private Integer holidayStaffBoost;

    private Integer status;

    private String remark;
}
