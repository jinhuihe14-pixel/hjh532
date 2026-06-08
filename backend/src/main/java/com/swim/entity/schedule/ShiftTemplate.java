package com.swim.entity.schedule;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shift_template")
public class ShiftTemplate extends BaseEntity {

    private String shiftName;

    private String shiftCode;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer breakDuration;

    private BigDecimal workHours;

    private String shiftType;

    private String color;

    private Integer sort;

    private Integer status;

    private String remark;
}
