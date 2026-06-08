package com.swim.service.member.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.entity.member.CardType;
import com.swim.entity.member.MemberAccount;
import com.swim.entity.member.MemberCard;
import com.swim.entity.member.MemberAccountLog;
import com.swim.mapper.member.CardTypeMapper;
import com.swim.mapper.member.MemberAccountLogMapper;
import com.swim.mapper.member.MemberCardMapper;
import com.swim.service.member.MemberAccountService;
import com.swim.service.member.MemberCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberCardServiceImpl extends ServiceImpl<MemberCardMapper, MemberCard>
        implements MemberCardService {

    private final CardTypeMapper cardTypeMapper;
    private final MemberAccountService memberAccountService;
    private final MemberAccountLogMapper accountLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberCard issueCard(Long memberId, Long subAccountId, Long cardTypeId,
                                String payType, Long saleId) {
        MemberAccount member = memberAccountService.getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        if (member.getStatus() != 1) {
            throw new BusinessException("会员账户已冻结");
        }

        CardType cardType = cardTypeMapper.selectById(cardTypeId);
        if (cardType == null || cardType.getStatus() != 1) {
            throw new BusinessException("卡种不存在或已停用");
        }

        if ("MEMBER".equals(payType)) {
            memberAccountService.consume(memberId, cardType.getPrice(), "MEMBER_CARD",
                    null, null);
        }

        MemberCard card = new MemberCard();
        card.setCardNo("C" + IdUtil.getSnowflakeNextIdStr());
        card.setMemberId(memberId);
        card.setSubAccountId(subAccountId);
        card.setCardTypeId(cardTypeId);
        card.setCardName(cardType.getCardName());
        card.setCardType(cardType.getCardType());
        card.setBuyPrice(cardType.getPrice());
        card.setPayType(payType);

        LocalDate startDate = LocalDate.now();
        card.setValidStartDate(startDate);
        card.setValidEndDate(startDate.plusDays(cardType.getValidDays()));

        if ("COUNT".equals(cardType.getCardType())) {
            card.setTotalCount(cardType.getTotalCount());
            card.setRemainingCount(cardType.getTotalCount());
        }

        if (cardType.getGiftHours() != null && cardType.getGiftHours().compareTo(BigDecimal.ZERO) > 0) {
            card.setTotalHours(cardType.getGiftHours());
            card.setRemainingHours(cardType.getGiftHours());
        }

        card.setStatus(1);
        card.setActivationTime(LocalDateTime.now());
        card.setSaleId(saleId);

        save(card);

        if (cardType.getGiftPrincipal() != null && cardType.getGiftPrincipal().compareTo(BigDecimal.ZERO) > 0) {
            memberAccountService.recharge(memberId, cardType.getGiftPrincipal(), BigDecimal.ZERO,
                    "办卡赠送本金-" + cardType.getCardName());
        }
        if (cardType.getGiftAmount() != null && cardType.getGiftAmount().compareTo(BigDecimal.ZERO) > 0) {
            memberAccountService.recharge(memberId, BigDecimal.ZERO, cardType.getGiftAmount(),
                    "办卡赠送金额-" + cardType.getCardName());
        }

        return card;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductCount(Long cardId, int count, String businessType,
                               Long businessId, String businessNo) {
        MemberCard card = getById(cardId);
        if (card == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (card.getStatus() != 1) {
            throw new BusinessException("会员卡状态异常");
        }
        if (LocalDate.now().isAfter(card.getValidEndDate())) {
            throw new BusinessException("会员卡已过期");
        }
        if (card.getRemainingCount() == null || card.getRemainingCount() < count) {
            throw new BusinessException("剩余次数不足");
        }

        int beforeCount = card.getRemainingCount();
        card.setRemainingCount(card.getRemainingCount() - count);

        if (card.getRemainingCount() == 0) {
            card.setStatus(3);
        }

        recordCardLog(card, "COUNT", "CONSUMPTION",
                BigDecimal.valueOf(-count), BigDecimal.valueOf(beforeCount),
                BigDecimal.valueOf(card.getRemainingCount()),
                businessType, businessId, businessNo, "扣次消费", null);

        return updateById(card);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductHours(Long cardId, BigDecimal hours, String businessType,
                               Long businessId, String businessNo) {
        MemberCard card = getById(cardId);
        if (card == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (card.getStatus() != 1) {
            throw new BusinessException("会员卡状态异常");
        }
        if (LocalDate.now().isAfter(card.getValidEndDate())) {
            throw new BusinessException("会员卡已过期");
        }
        if (card.getRemainingHours() == null || card.getRemainingHours().compareTo(hours) < 0) {
            throw new BusinessException("剩余课时不足");
        }

        BigDecimal beforeHours = card.getRemainingHours();
        card.setRemainingHours(card.getRemainingHours().subtract(hours));

        if (card.getRemainingHours().compareTo(BigDecimal.ZERO) <= 0 &&
                (card.getRemainingCount() == null || card.getRemainingCount() <= 0)) {
            card.setStatus(3);
        }

        recordCardLog(card, "HOURS", "CONSUMPTION",
                hours.negate(), beforeHours, card.getRemainingHours(),
                businessType, businessId, businessNo, "扣课时", null);

        return updateById(card);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean extendCard(Long cardId, int days, String reason) {
        MemberCard card = getById(cardId);
        if (card == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (card.getStatus() == 3) {
            throw new BusinessException("已用完的卡不能延期");
        }

        LocalDate oldEndDate = card.getValidEndDate();
        card.setValidEndDate(card.getValidEndDate().plusDays(days));

        if (card.getStatus() == 2 && LocalDate.now().isBefore(card.getValidEndDate())) {
            card.setStatus(1);
        }

        recordCardLog(card, null, "ADJUST", BigDecimal.valueOf(days),
                BigDecimal.valueOf(oldEndDate.toEpochDay()),
                BigDecimal.valueOf(card.getValidEndDate().toEpochDay()),
                null, null, null, "卡延期-" + days + "天", reason);

        return updateById(card);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferCard(Long cardId, Long targetMemberId, Long targetSubAccountId) {
        MemberCard card = getById(cardId);
        if (card == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (card.getStatus() != 1) {
            throw new BusinessException("会员卡状态异常，无法转卡");
        }

        MemberAccount targetMember = memberAccountService.getById(targetMemberId);
        if (targetMember == null || targetMember.getStatus() != 1) {
            throw new BusinessException("目标会员不存在或已冻结");
        }

        Long oldMemberId = card.getMemberId();
        Long oldSubAccountId = card.getSubAccountId();

        card.setMemberId(targetMemberId);
        card.setSubAccountId(targetSubAccountId);

        recordCardLog(card, null, "TRANSFER", BigDecimal.ZERO,
                BigDecimal.valueOf(oldMemberId), BigDecimal.valueOf(targetMemberId),
                null, null, null, "转卡", "原会员:" + oldMemberId + ",子账户:" + oldSubAccountId);

        return updateById(card);
    }

    private void recordCardLog(MemberCard card, String accountType, String changeType,
                               BigDecimal changeAmount, BigDecimal beforeBalance,
                               BigDecimal afterBalance, String businessType,
                               Long businessId, String businessNo, String operatorName,
                               String remark) {
        MemberAccountLog log = new MemberAccountLog();
        log.setLogNo("LOG" + IdUtil.getSnowflakeNextIdStr());
        log.setMemberId(card.getMemberId());
        log.setSubAccountId(card.getSubAccountId());
        log.setCardId(card.getId());
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
