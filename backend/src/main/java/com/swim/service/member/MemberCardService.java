package com.swim.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.member.MemberCard;

public interface MemberCardService extends IService<MemberCard> {

    MemberCard issueCard(Long memberId, Long subAccountId, Long cardTypeId, String payType, Long saleId);

    boolean deductCount(Long cardId, int count, String businessType, Long businessId, String businessNo);

    boolean deductHours(Long cardId, java.math.BigDecimal hours, String businessType,
                        Long businessId, String businessNo);

    boolean extendCard(Long cardId, int days, String reason);

    boolean transferCard(Long cardId, Long targetMemberId, Long targetSubAccountId);
}
