package com.swim.entity.member;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_account_log")
public class MemberAccountLog {

    private Long id;

    private String logNo;

    private Long memberId;

    private Long subAccountId;

    private Long cardId;

    private String accountType;

    private String changeType;

    private BigDecimal changeAmount;

    private BigDecimal beforeBalance;

    private BigDecimal afterBalance;

    private String businessType;

    private Long businessId;

    private String businessNo;

    private Long operatorId;

    private String operatorName;

    private String remark;

    private LocalDateTime createTime;
}
