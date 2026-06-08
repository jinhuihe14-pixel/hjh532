package com.swim.entity.member;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_account")
public class MemberAccount extends BaseEntity {

    private String memberNo;

    private String memberName;

    private String phone;

    private String idCard;

    private String avatar;

    private Integer gender;

    private LocalDate birthday;

    private String address;

    private BigDecimal principalBalance;

    private BigDecimal giftBalance;

    private BigDecimal totalRecharge;

    private BigDecimal totalConsumption;

    private String memberLevel;

    private Integer status;

    private String sourceType;

    private String remark;
}
