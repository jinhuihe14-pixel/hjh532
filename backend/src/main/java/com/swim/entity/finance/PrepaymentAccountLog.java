package com.swim.entity.finance;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prepayment_account_log")
public class PrepaymentAccountLog extends BaseEntity {

    private String logNo;

    private Long accountId;

    private String accountNo;

    private Long customerId;

    private String customerName;

    private String accountType;

    private String balanceType;

    private String changeType;

    private BigDecimal changeAmount;

    private BigDecimal beforeBalance;

    private BigDecimal afterBalance;

    private String businessType;

    private Long businessId;

    private String businessNo;

    private Long relatedLogId;

    private Long operatorId;

    private String operatorName;

    private String remark;

    private LocalDateTime createTime;
}
