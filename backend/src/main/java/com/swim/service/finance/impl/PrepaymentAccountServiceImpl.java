package com.swim.service.finance.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.finance.PrepaymentAccount;
import com.swim.mapper.finance.PrepaymentAccountMapper;
import com.swim.service.finance.PrepaymentAccountLogService;
import com.swim.service.finance.PrepaymentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrepaymentAccountServiceImpl extends ServiceImpl<PrepaymentAccountMapper, PrepaymentAccount> implements PrepaymentAccountService {

    private final PrepaymentAccountLogService logService;

    @Override
    public Page<PrepaymentAccount> getAccountPage(PageQuery query, String accountType, String customerType,
                                                   Long customerId, Integer status, String keyword) {
        Page<PrepaymentAccount> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PrepaymentAccount> wrapper = new LambdaQueryWrapper<>();
        if (accountType != null && !accountType.isEmpty()) {
            wrapper.eq(PrepaymentAccount::getAccountType, accountType);
        }
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(PrepaymentAccount::getCustomerType, customerType);
        }
        if (customerId != null) {
            wrapper.eq(PrepaymentAccount::getCustomerId, customerId);
        }
        if (status != null) {
            wrapper.eq(PrepaymentAccount::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PrepaymentAccount::getAccountName, keyword)
                    .or().like(PrepaymentAccount::getCustomerName, keyword);
        }
        wrapper.orderByDesc(PrepaymentAccount::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public PrepaymentAccount createAccount(PrepaymentAccount account) {
        account.setAccountNo("PA" + IdUtil.getSnowflakeNextIdStr());
        if (account.getPrincipalBalance() == null) {
            account.setPrincipalBalance(BigDecimal.ZERO);
        }
        if (account.getGiftBalance() == null) {
            account.setGiftBalance(BigDecimal.ZERO);
        }
        if (account.getFrozenAmount() == null) {
            account.setFrozenAmount(BigDecimal.ZERO);
        }
        if (account.getTotalRecharge() == null) {
            account.setTotalRecharge(BigDecimal.ZERO);
        }
        if (account.getTotalConsumption() == null) {
            account.setTotalConsumption(BigDecimal.ZERO);
        }
        if (account.getTotalGift() == null) {
            account.setTotalGift(BigDecimal.ZERO);
        }
        if (account.getStatus() == null) {
            account.setStatus(1);
        }
        if (account.getOpenDate() == null) {
            account.setOpenDate(LocalDate.now());
        }
        save(account);
        return account;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recharge(Long accountId, BigDecimal amount, BigDecimal giftAmount, String businessType,
                            Long businessId, String businessNo, Long operatorId, String operatorName, String remark) {
        PrepaymentAccount account = getById(accountId);
        if (account == null) {
            throw new BusinessException("预存账户不存在");
        }
        if (account.getStatus() != 1) {
            throw new BusinessException("账户状态不正常，无法充值");
        }

        BigDecimal beforePrincipal = account.getPrincipalBalance();
        BigDecimal beforeGift = account.getGiftBalance();

        account.setPrincipalBalance(account.getPrincipalBalance().add(amount));
        account.setTotalRecharge(account.getTotalRecharge().add(amount));

        if (giftAmount != null && giftAmount.compareTo(BigDecimal.ZERO) > 0) {
            account.setGiftBalance(account.getGiftBalance().add(giftAmount));
            account.setTotalGift(account.getTotalGift().add(giftAmount));
        }

        updateById(account);

        logService.addLog(accountId, account.getAccountNo(), account.getCustomerId(), account.getCustomerName(),
                account.getAccountType(), "PRINCIPAL", "RECHARGE", amount,
                beforePrincipal, account.getPrincipalBalance(),
                businessType, businessId, businessNo,
                operatorId, operatorName, remark);

        if (giftAmount != null && giftAmount.compareTo(BigDecimal.ZERO) > 0) {
            logService.addLog(accountId, account.getAccountNo(), account.getCustomerId(), account.getCustomerName(),
                    account.getAccountType(), "GIFT", "GIFT", giftAmount,
                    beforeGift, account.getGiftBalance(),
                    businessType, businessId, businessNo,
                    operatorId, operatorName, "赠送金额");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean consume(Long accountId, BigDecimal amount, String balanceType, String businessType,
                           Long businessId, String businessNo, Long operatorId, String operatorName, String remark) {
        PrepaymentAccount account = getById(accountId);
        if (account == null) {
            throw new BusinessException("预存账户不存在");
        }
        if (account.getStatus() != 1) {
            throw new BusinessException("账户状态不正常，无法消费");
        }

        BigDecimal beforeBalance;
        BigDecimal afterBalance;

        if ("GIFT".equals(balanceType)) {
            if (account.getGiftBalance().compareTo(amount) < 0) {
                throw new BusinessException("赠金余额不足");
            }
            beforeBalance = account.getGiftBalance();
            account.setGiftBalance(account.getGiftBalance().subtract(amount));
            afterBalance = account.getGiftBalance();
        } else {
            BigDecimal available = account.getPrincipalBalance().subtract(account.getFrozenAmount());
            if (available.compareTo(amount) < 0) {
                throw new BusinessException("本金余额不足");
            }
            beforeBalance = account.getPrincipalBalance();
            account.setPrincipalBalance(account.getPrincipalBalance().subtract(amount));
            afterBalance = account.getPrincipalBalance();
        }

        account.setTotalConsumption(account.getTotalConsumption().add(amount));
        updateById(account);

        logService.addLog(accountId, account.getAccountNo(), account.getCustomerId(), account.getCustomerName(),
                account.getAccountType(), balanceType, "CONSUMPTION", amount,
                beforeBalance, afterBalance,
                businessType, businessId, businessNo,
                operatorId, operatorName, remark);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refund(Long accountId, BigDecimal amount, String businessType, Long businessId,
                          String businessNo, Long operatorId, String operatorName, String remark) {
        PrepaymentAccount account = getById(accountId);
        if (account == null) {
            throw new BusinessException("预存账户不存在");
        }

        BigDecimal beforeBalance = account.getPrincipalBalance();
        account.setPrincipalBalance(account.getPrincipalBalance().add(amount));
        updateById(account);

        logService.addLog(accountId, account.getAccountNo(), account.getCustomerId(), account.getCustomerName(),
                account.getAccountType(), "PRINCIPAL", "REFUND", amount,
                beforeBalance, account.getPrincipalBalance(),
                businessType, businessId, businessNo,
                operatorId, operatorName, remark);

        return true;
    }

    @Override
    public boolean freeze(Long accountId, BigDecimal amount, String remark) {
        PrepaymentAccount account = getById(accountId);
        if (account == null) {
            throw new BusinessException("预存账户不存在");
        }
        BigDecimal available = account.getPrincipalBalance().subtract(account.getFrozenAmount());
        if (available.compareTo(amount) < 0) {
            throw new BusinessException("可用余额不足，无法冻结");
        }
        account.setFrozenAmount(account.getFrozenAmount().add(amount));
        return updateById(account);
    }

    @Override
    public boolean unfreeze(Long accountId, BigDecimal amount, String remark) {
        PrepaymentAccount account = getById(accountId);
        if (account == null) {
            throw new BusinessException("预存账户不存在");
        }
        if (account.getFrozenAmount().compareTo(amount) < 0) {
            throw new BusinessException("冻结金额不足，无法解冻");
        }
        account.setFrozenAmount(account.getFrozenAmount().subtract(amount));
        return updateById(account);
    }

    @Override
    public PrepaymentAccount getByCustomer(String customerType, Long customerId, String accountType) {
        LambdaQueryWrapper<PrepaymentAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrepaymentAccount::getCustomerType, customerType);
        wrapper.eq(PrepaymentAccount::getCustomerId, customerId);
        wrapper.eq(PrepaymentAccount::getAccountType, accountType);
        return getOne(wrapper);
    }

    @Override
    public BigDecimal getTotalBalance(String accountType) {
        LambdaQueryWrapper<PrepaymentAccount> wrapper = new LambdaQueryWrapper<>();
        if (accountType != null && !accountType.isEmpty()) {
            wrapper.eq(PrepaymentAccount::getAccountType, accountType);
        }
        wrapper.eq(PrepaymentAccount::getStatus, 1);
        List<PrepaymentAccount> accounts = list(wrapper);
        return accounts.stream()
                .map(a -> {
                    BigDecimal p = a.getPrincipalBalance() != null ? a.getPrincipalBalance() : BigDecimal.ZERO;
                    BigDecimal g = a.getGiftBalance() != null ? a.getGiftBalance() : BigDecimal.ZERO;
                    return p.add(g);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
