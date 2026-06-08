package com.swim.entity.schedule;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("holiday_config")
public class HolidayConfig extends BaseEntity {

    private LocalDate holidayDate;

    private String holidayName;

    private String holidayType;

    private Integer year;

    private String remark;
}
