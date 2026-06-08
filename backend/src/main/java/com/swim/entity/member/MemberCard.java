package com.swim.entity.member;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_card")
public class MemberCard extends BaseEntity {

    private String cardNo;

    private Long memberId;

    private Long subAccountId;

    private Long cardTypeId;

    private String cardName;

    private String cardType;

    private BigDecimal buyPrice;

    private String payType;

    private LocalDate validStartDate;

    private LocalDate validEndDate;

    private Integer totalCount;

    private Integer remainingCount;

    private BigDecimal totalHours;

    private BigDecimal remainingHours;

    private Integer status;

    private LocalDateTime activationTime;

    private Long saleId;

    private String remark;
}
