package com.swim.entity.finance;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("receivable_settlement")
public class ReceivableSettlement extends BaseEntity {

    private String settlementNo;

    private String customerType;

    private Long customerId;

    private String customerName;

    private String settlementPeriod;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private BigDecimal openingBalance;

    private BigDecimal periodAddAmount;

    private BigDecimal periodReceivedAmount;

    private BigDecimal closingBalance;

    private Integer billCount;

    private Integer paidBillCount;

    private Integer unpaidBillCount;

    private Integer overdueBillCount;

    private BigDecimal overdueAmount;

    private Integer settlementStatus;

    private LocalDateTime confirmTime;

    private Long confirmerId;

    private String confirmerName;

    private String remark;
}
