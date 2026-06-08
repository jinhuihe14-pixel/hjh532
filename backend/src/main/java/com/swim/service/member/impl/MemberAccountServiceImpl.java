package com.swim.service.member.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.entity.member.MemberAccount;
import com.swim.entity.member.MemberAccountLog;
import com.swim.mapper.member.MemberAccountLogMapper;
import com.swim.mapper.member.MemberAccountMapper;
import com.swim.service.member.MemberAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAccountServiceImpl extends ServiceImpl<MemberAccountMapper, MemberAccount>
        implements MemberAccountService {

    private final MemberAccountLogMapper accountLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberAccount register(MemberAccount member) {
        LambdaQueryWrapper<MemberAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberAccount::getPhone, member.getPhone());
        Long count = count(wrapper);
        if (count > 0) {
            throw new BusinessException("该手机号已注册");
        }

        member.setMemberNo("M" + IdUtil.getSnowflakeNextIdStr().substring(0, 10));
        member.setPrincipalBalance(BigDecimal.ZERO);
        member.setGiftBalance(BigDecimal.ZERO);
        member.setTotalRecharge(BigDecimal.ZERO);
        member.setTotalConsumption(BigDecimal.ZERO);
        if (member.getMemberLevel() == null) {
            member.setMemberLevel("NORMAL");
        }
        if (member.getStatus() == null) {
            member.setStatus(1);
        }

        save(member);
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recharge(Long memberId, BigDecimal principalAmount, BigDecimal giftAmount, String remark) {
        MemberAccount member = getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        if (member.getStatus() != 1) {
            throw new BusinessException("会员账户已冻结");
        }

        BigDecimal beforePrincipal = member.getPrincipalBalance();
        BigDecimal beforeGift = member.getGiftBalance();

        if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
            member.setPrincipalBalance(member.getPrincipalBalance().add(principalAmount));
            member.setTotalRecharge(member.getTotalRecharge().add(principalAmount));
            recordAccountLog(memberId, null, null, "PRINCIPAL", "RECHARGE",
                    principalAmount, beforePrincipal, member.getPrincipalBalance(),
                    null, null, null, "储值本金充值", remark);
        }

        if (giftAmount != null && giftAmount.compareTo(BigDecimal.ZERO) > 0) {
            member.setGiftBalance(member.getGiftBalance().add(giftAmount));
            recordAccountLog(memberId, null, null, "GIFT", "PRESENT",
                    giftAmount, beforeGift, member.getGiftBalance(),
                    null, null, null, "赠送余额充值", remark);
        }

        return updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean consume(Long memberId, BigDecimal amount, String businessType,
                           Long businessId, String businessNo) {
        MemberAccount member = getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        if (member.getStatus() != 1) {
            throw new BusinessException("会员账户已冻结");
        }

        BigDecimal totalBalance = member.getPrincipalBalance().add(member.getGiftBalance());
        if (totalBalance.compareTo(amount) < 0) {
            throw new BusinessException("账户余额不足");
        }

        BigDecimal remainingAmount = amount;

        BigDecimal giftBefore = member.getGiftBalance();
        BigDecimal giftDeduct = remainingAmount.min(member.getGiftBalance());
        if (giftDeduct.compareTo(BigDecimal.ZERO) > 0) {
            member.setGiftBalance(member.getGiftBalance().subtract(giftDeduct));
            recordAccountLog(memberId, null, null, "GIFT", "CONSUMPTION",
                    giftDeduct.negate(), giftBefore, member.getGiftBalance(),
                    businessType, businessId, businessNo, "消费扣减-赠送余额", null);
            remainingAmount = remainingAmount.subtract(giftDeduct);
        }

        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal principalBefore = member.getPrincipalBalance();
            member.setPrincipalBalance(member.getPrincipalBalance().subtract(remainingAmount));
            recordAccountLog(memberId, null, null, "PRINCIPAL", "CONSUMPTION",
                    remainingAmount.negate(), principalBefore, member.getPrincipalBalance(),
                    businessType, businessId, businessNo, "消费扣减-储值本金", null);
        }

        member.setTotalConsumption(member.getTotalConsumption().add(amount));

        return updateById(member);
    }

    private void recordAccountLog(Long memberId, Long subAccountId, Long cardId,
                                  String accountType, String changeType,
                                  BigDecimal changeAmount, BigDecimal beforeBalance,
                                  BigDecimal afterBalance, String businessType,
                                  Long businessId, String businessNo, String operatorName,
                                  String remark) {
        MemberAccountLog log = new MemberAccountLog();
        log.setLogNo("LOG" + IdUtil.getSnowflakeNextIdStr());
        log.setMemberId(memberId);
        log.setSubAccountId(subAccountId);
        log.setCardId(cardId);
        log.setAccountType(accountType);
        log.setChangeType(changeType);
        log.setChangeAmount(changeAmount);
        log.setBeforeBalance(beforeBalance);
        log.setAfterBalance(afterBalance);
        log.setBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setBusinessNo(businessNo);
        log.setOperatorName(operatorName);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        accountLogMapper.insert(log);
    }
}
