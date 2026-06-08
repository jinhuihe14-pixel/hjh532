package com.swim.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.member.MemberAccount;
import java.math.BigDecimal;

public interface MemberAccountService extends IService<MemberAccount> {

    MemberAccount register(MemberAccount member);

    boolean recharge(Long memberId, BigDecimal principalAmount, BigDecimal giftAmount, String remark);

    boolean consume(Long memberId, BigDecimal amount, String businessType, Long businessId, String businessNo);
}
