package com.swim.entity.schedule;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employee_schedule_preference")
public class EmployeeSchedulePreference extends BaseEntity {

    private Long employeeId;

    private String preferredShift;

    private String preferredRestDay;

    private String unavailableDays;

    private BigDecimal maxWeeklyHours;

    private Integer nightShiftAllowed;

    private Integer weekendWorkAllowed;

    private Integer holidayWorkAllowed;

    private String remark;
}
