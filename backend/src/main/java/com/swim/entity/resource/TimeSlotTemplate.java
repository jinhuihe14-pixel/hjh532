package com.swim.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("time_slot_template")
public class TimeSlotTemplate extends BaseEntity {

    private String templateName;

    private Long venueId;

    private LocalTime startTime;

    private LocalTime endTime;

    private String usageType;

    private BigDecimal price;

    private Integer sort;

    private Integer status;
}
