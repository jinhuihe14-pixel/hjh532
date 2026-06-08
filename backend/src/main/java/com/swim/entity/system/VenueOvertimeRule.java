package com.swim.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("venue_overtime_rule")
public class VenueOvertimeRule extends BaseEntity {

    private String ruleName;

    private String venueType;

    private Long venueId;

    private String rentalType;

    private Integer graceMinutes;

    private Integer billingUnit;

    private BigDecimal overtimeRate;

    private String rateType;

    private BigDecimal maxOvertimeAmount;

    private Integer maxOvertimeMinutes;

    private Integer isAutomatic;

    private Integer status;

    private String remark;
}
