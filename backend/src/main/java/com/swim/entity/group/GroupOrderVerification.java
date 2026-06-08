package com.swim.entity.group;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("group_order_verification")
public class GroupOrderVerification extends BaseEntity {

    private String verificationNo;

    private Long orderId;

    private String orderNo;

    private Long customerId;

    private String customerName;

    private Long venueId;

    private String venueName;

    private LocalDate verifyDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer actualPeople;

    private Integer usedCount;

    private BigDecimal unitPrice;

    private BigDecimal verifyAmount;

    private Integer verifyStatus;

    private LocalDateTime verifyTime;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime cancelTime;

    private String cancelReason;

    private String remark;
}
