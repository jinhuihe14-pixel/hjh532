package com.swim.entity.group;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("group_order_extension")
public class GroupOrderExtension extends BaseEntity {

    private String extensionNo;

    private Long orderId;

    private String orderNo;

    private LocalDate originalEndDate;

    private LocalDate newEndDate;

    private Integer extensionDays;

    private BigDecimal extensionFee;

    private String applyReason;

    private Long applicantId;

    private String applicantName;

    private Integer approvalStatus;

    private Long approverId;

    private String approverName;

    private LocalDateTime approvalTime;

    private String approvalOpinion;

    private String remark;
}
