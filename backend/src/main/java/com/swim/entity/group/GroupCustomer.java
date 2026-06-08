package com.swim.entity.group;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("group_customer")
public class GroupCustomer extends BaseEntity {

    private String customerNo;

    private String customerName;

    private String customerType;

    private String industry;

    private String contactName;

    private String contactPhone;

    private String contactPosition;

    private String secondaryContact;

    private String secondaryPhone;

    private String address;

    private String email;

    private String settlementType;

    private BigDecimal creditLimit;

    private Integer accountPeriod;

    private BigDecimal discountRate;

    private String customerLevel;

    private Integer totalOrders;

    private BigDecimal totalAmount;

    private Integer status;

    private String remark;
}
