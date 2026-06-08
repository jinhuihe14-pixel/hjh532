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
@TableName("receivable_payment")
public class ReceivablePayment extends BaseEntity {

    private String paymentNo;

    private Long billId;

    private String billNo;

    private Long customerId;

    private String customerName;

    private BigDecimal paymentAmount;

    private String paymentType;

    private LocalDate paymentDate;

    private LocalDateTime paymentTime;

    private String payAccount;

    private String receiveAccount;

    private String voucherNo;

    private Integer isPartial;

    private Integer paymentStatus;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime confirmTime;

    private Long confirmerId;

    private String confirmerName;

    private String remark;
}
