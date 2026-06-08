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
@TableName("group_order")
public class GroupOrder extends BaseEntity {

    private String orderNo;

    private String orderName;

    private Long customerId;

    private String customerName;

    private String contactName;

    private String contactPhone;

    private String orderType;

    private Integer totalPeople;

    private Integer estimatedVisits;

    private Integer usedVisits;

    private Integer remainingVisits;

    private Long venueId;

    private String venueName;

    private LocalDate validStartDate;

    private LocalDate validEndDate;

    private BigDecimal exclusivePrice;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal payableAmount;

    private BigDecimal depositAmount;

    private BigDecimal paidAmount;

    private BigDecimal unpaidAmount;

    private Integer payStatus;

    private Integer orderStatus;

    private LocalDateTime confirmTime;

    private LocalDateTime finishTime;

    private Long salesId;

    private String salesName;

    private Long serviceStaffId;

    private String serviceStaffName;

    private Integer isVenueLocked;

    private Integer lockPriority;

    private String requirement;

    private String remark;
}
