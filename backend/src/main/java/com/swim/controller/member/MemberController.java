package com.swim.controller.member;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swim.common.PageQuery;
import com.swim.common.Result;
import com.swim.entity.member.MemberAccount;
import com.swim.entity.member.MemberCard;
import com.swim.entity.member.CardType;
import com.swim.entity.member.MemberSubAccount;
import com.swim.service.member.MemberAccountService;
import com.swim.service.member.MemberCardService;
import com.swim.service.member.CardTypeService;
import com.swim.service.member.MemberSubAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "会员管理")
public class MemberController {

    private final MemberAccountService memberAccountService;
    private final MemberCardService memberCardService;
    private final CardTypeService cardTypeService;
    private final MemberSubAccountService memberSubAccountService;

    @GetMapping("/account/page")
    public Result<Page<MemberAccount>> getMemberPage(PageQuery query, String keyword, String memberLevel, Integer status) {
        Page<MemberAccount> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<MemberAccount> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(MemberAccount::getMemberName, keyword)
                    .or().like(MemberAccount::getPhone, keyword)
                    .or().like(MemberAccount::getMemberNo, keyword));
        }
        if (memberLevel != null && !memberLevel.isEmpty()) {
            wrapper.eq(MemberAccount::getMemberLevel, memberLevel);
        }
        if (status != null) {
            wrapper.eq(MemberAccount::getStatus, status);
        }
        wrapper.orderByDesc(MemberAccount::getCreateTime);
        return Result.success(memberAccountService.page(page, wrapper));
    }

    @GetMapping("/account/{id}")
    public Result<MemberAccount> getMember(@PathVariable Long id) {
        return Result.success(memberAccountService.getById(id));
    }

    @PostMapping("/account/register")
    public Result<MemberAccount> register(@RequestBody MemberAccount member) {
        return Result.success(memberAccountService.register(member));
    }

    @PutMapping("/account")
    public Result<Void> updateMember(@RequestBody MemberAccount member) {
        memberAccountService.updateById(member);
        return Result.success();
    }

    @PostMapping("/account/{id}/recharge")
    public Result<Void> recharge(@PathVariable Long id,
                                 @RequestParam(required = false) BigDecimal principalAmount,
                                 @RequestParam(required = false) BigDecimal giftAmount,
                                 @RequestParam(required = false) String remark) {
        memberAccountService.recharge(id, principalAmount, giftAmount, remark);
        return Result.success();
    }

    @PostMapping("/account/{id}/consume")
    public Result<Void> consume(@PathVariable Long id,
                                @RequestParam BigDecimal amount,
                                @RequestParam(required = false) String businessType,
                                @RequestParam(required = false) Long businessId,
                                @RequestParam(required = false) String businessNo) {
        memberAccountService.consume(id, amount, businessType, businessId, businessNo);
        return Result.success();
    }

    @GetMapping("/sub-account/{memberId}")
    public Result<List<MemberSubAccount>> getSubAccounts(@PathVariable Long memberId) {
        LambdaQueryWrapper<MemberSubAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberSubAccount::getMemberId, memberId);
        wrapper.eq(MemberSubAccount::getStatus, 1);
        wrapper.orderByAsc(MemberSubAccount::getId);
        return Result.success(memberSubAccountService.list(wrapper));
    }

    @PostMapping("/sub-account")
    public Result<Void> addSubAccount(@RequestBody MemberSubAccount subAccount) {
        memberSubAccountService.save(subAccount);
        return Result.success();
    }

    @GetMapping("/card-type/list")
    public Result<List<CardType>> getCardTypeList(String cardType) {
        LambdaQueryWrapper<CardType> wrapper = new LambdaQueryWrapper<>();
        if (cardType != null && !cardType.isEmpty()) {
            wrapper.eq(CardType::getCardType, cardType);
        }
        wrapper.eq(CardType::getStatus, 1);
        wrapper.orderByAsc(CardType::getSort);
        return Result.success(cardTypeService.list(wrapper));
    }

    @GetMapping("/card/list/{memberId}")
    public Result<List<MemberCard>> getMemberCards(@PathVariable Long memberId, Integer status) {
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getMemberId, memberId);
        if (status != null) {
            wrapper.eq(MemberCard::getStatus, status);
        }
        wrapper.orderByDesc(MemberCard::getCreateTime);
        return Result.success(memberCardService.list(wrapper));
    }

    @PostMapping("/card/issue")
    public Result<MemberCard> issueCard(@RequestParam Long memberId,
                                        @RequestParam(required = false) Long subAccountId,
                                        @RequestParam Long cardTypeId,
                                        @RequestParam String payType,
                                        @RequestParam(required = false) Long saleId) {
        return Result.success(memberCardService.issueCard(memberId, subAccountId, cardTypeId, payType, saleId));
    }

    @PostMapping("/card/{id}/extend")
    public Result<Void> extendCard(@PathVariable Long id,
                                   @RequestParam int days,
                                   @RequestParam(required = false) String reason) {
        memberCardService.extendCard(id, days, reason);
        return Result.success();
    }

    @PostMapping("/card/{id}/transfer")
    public Result<Void> transferCard(@PathVariable Long id,
                                     @RequestParam Long targetMemberId,
                                     @RequestParam(required = false) Long targetSubAccountId) {
        memberCardService.transferCard(id, targetMemberId, targetSubAccountId);
        return Result.success();
    }
}
