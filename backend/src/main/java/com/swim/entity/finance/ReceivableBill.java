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
@TableName("receivable_bill")
public class ReceivableBill extends BaseEntity {

    private String billNo;

    private String billType;

    private String customerType;

    private Long customerId;

    private String customerName;

    private String contactName;

    private String contactPhone;

    private String businessType;

    private Long businessId;

    private String businessNo;

    private BigDecimal billAmount;

    private BigDecimal discountAmount;

    private BigDecimal receivableAmount;

    private BigDecimal paidAmount;

    private BigDecimal unpaidAmount;

    private LocalDate billDate;

    private LocalDate dueDate;

    private Integer accountPeriod;

    private String settlementCycle;

    private Integer billStatus;

    private Integer remindCount;

    private LocalDateTime lastRemindTime;

    private String remark;
}
