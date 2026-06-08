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
@TableName("deposit_order")
public class DepositOrder extends BaseEntity {

    private String depositNo;

    private String customerType;

    private Long customerId;

    private String customerName;

    private String contactName;

    private String contactPhone;

    private String businessType;

    private Long businessId;

    private String businessNo;

    private BigDecimal depositAmount;

    private String depositType;

    private Integer payStatus;

    private LocalDateTime payTime;

    private String payType;

    private Integer deductStatus;

    private BigDecimal deductAmount;

    private BigDecimal remainingAmount;

    private BigDecimal refundAmount;

    private LocalDateTime refundTime;

    private LocalDate expireDate;

    private Integer depositStatus;

    private Long operatorId;

    private String operatorName;

    private String remark;
}
