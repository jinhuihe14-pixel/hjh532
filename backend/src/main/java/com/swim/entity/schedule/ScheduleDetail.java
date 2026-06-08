package com.swim.entity.schedule;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("schedule_detail")
public class ScheduleDetail extends BaseEntity {

    private Long planId;

    private Long employeeId;

    private String employeeName;

    private String employeeNo;

    private String positionType;

    private Long shiftId;

    private String shiftCode;

    private String shiftName;

    private LocalDate scheduleDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal workHours;

    private Integer isWeekend;

    private Integer isHoliday;

    private Integer isOvertime;

    private String scheduleSource;

    private String remark;
}
