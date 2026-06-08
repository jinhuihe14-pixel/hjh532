package com.swim.entity.finance;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prepayment_account")
public class PrepaymentAccount extends BaseEntity {

    private String accountNo;

    private String accountName;

    private String accountType;

    private String customerType;

    private Long customerId;

    private String customerName;

    private BigDecimal principalBalance;

    private BigDecimal giftBalance;

    private BigDecimal frozenAmount;

    private BigDecimal totalRecharge;

    private BigDecimal totalConsumption;

    private BigDecimal totalGift;

    private Integer status;

    private LocalDate openDate;

    private LocalDate expireDate;

    private String remark;
}
