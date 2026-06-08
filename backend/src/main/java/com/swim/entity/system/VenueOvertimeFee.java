package com.swim.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("venue_overtime_fee")
public class VenueOvertimeFee extends BaseEntity {

    private String feeNo;

    private Long rentalOrderId;

    private String orderNo;

    private Long venueId;

    private String venueName;

    private String customerName;

    private String customerPhone;

    private LocalDateTime scheduledEndTime;

    private LocalDateTime actualEndTime;

    private Integer overtimeMinutes;

    private Integer billingUnits;

    private BigDecimal unitPrice;

    private BigDecimal overtimeAmount;

    private Integer graceMinutes;

    private Long ruleId;

    private Integer payStatus;

    private LocalDateTime payTime;

    private String payType;

    private Long deductionAccountId;

    private Long operatorId;

    private String operatorName;

    private Integer feeStatus;

    private String waiverReason;

    private String remark;
}
