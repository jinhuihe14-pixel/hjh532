package com.swim.service.finance.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.finance.DepositOrder;
import com.swim.mapper.finance.DepositOrderMapper;
import com.swim.service.finance.DepositOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepositOrderServiceImpl extends ServiceImpl<DepositOrderMapper, DepositOrder> implements DepositOrderService {

    @Override
    public Page<DepositOrder> getDepositPage(PageQuery query, String customerType, Long customerId,
                                              String businessType, Integer depositStatus, Integer payStatus) {
        Page<DepositOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DepositOrder> wrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(DepositOrder::getCustomerType, customerType);
        }
        if (customerId != null) {
            wrapper.eq(DepositOrder::getCustomerId, customerId);
        }
        if (businessType != null && !businessType.isEmpty()) {
            wrapper.eq(DepositOrder::getBusinessType, businessType);
        }
        if (depositStatus != null) {
            wrapper.eq(DepositOrder::getDepositStatus, depositStatus);
        }
        if (payStatus != null) {
            wrapper.eq(DepositOrder::getPayStatus, payStatus);
        }
        wrapper.orderByDesc(DepositOrder::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public DepositOrder createDeposit(DepositOrder deposit) {
        deposit.setDepositNo("DO" + IdUtil.getSnowflakeNextIdStr());
        deposit.setPayStatus(0);
        deposit.setDepositStatus(1);
        if (deposit.getDeductStatus() == null) {
            deposit.setDeductStatus(0);
        }
        if (deposit.getDeductAmount() == null) {
            deposit.setDeductAmount(BigDecimal.ZERO);
        }
        if (deposit.getRemainingAmount() == null) {
            deposit.setRemainingAmount(deposit.getDepositAmount());
        }
        if (deposit.getRefundAmount() == null) {
            deposit.setRefundAmount(BigDecimal.ZERO);
        }
        save(deposit);
        return deposit;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payDeposit(Long id, String payType) {
        DepositOrder deposit = getById(id);
        if (deposit == null) {
            throw new BusinessException("定金单不存在");
        }
        if (deposit.getPayStatus() != 0) {
            throw new BusinessException("定金已支付，不可重复支付");
        }
        deposit.setPayStatus(1);
        deposit.setPayTime(LocalDateTime.now());
        deposit.setPayType(payType);
        return updateById(deposit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductDeposit(Long id, BigDecimal deductAmount, String businessType, Long businessId, String businessNo) {
        DepositOrder deposit = getById(id);
        if (deposit == null) {
            throw new BusinessException("定金单不存在");
        }
        if (deposit.getPayStatus() != 1) {
            throw new BusinessException("定金未支付，不可抵扣");
        }
        if (deposit.getDepositStatus() != 1) {
            throw new BusinessException("定金状态不正常，不可抵扣");
        }
        if (deposit.getRemainingAmount() == null || deposit.getRemainingAmount().compareTo(deductAmount) < 0) {
            throw new BusinessException("定金余额不足");
        }

        BigDecimal newDeductAmount = deposit.getDeductAmount().add(deductAmount);
        BigDecimal newRemaining = deposit.getDepositAmount().subtract(newDeductAmount);

        deposit.setDeductAmount(newDeductAmount);
        deposit.setRemainingAmount(newRemaining);

        if (newRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            deposit.setDeductStatus(2);
            deposit.setDepositStatus(2);
        } else {
            deposit.setDeductStatus(1);
        }

        return updateById(deposit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundDeposit(Long id, BigDecimal refundAmount, String reason) {
        DepositOrder deposit = getById(id);
        if (deposit == null) {
            throw new BusinessException("定金单不存在");
        }
        if (deposit.getPayStatus() != 1) {
            throw new BusinessException("定金未支付，不可退还");
        }
        BigDecimal availableRefund = deposit.getRemainingAmount();
        if (availableRefund == null || availableRefund.compareTo(refundAmount) < 0) {
            throw new BusinessException("可退还金额不足");
        }

        deposit.setRefundAmount(deposit.getRefundAmount().add(refundAmount));
        deposit.setRemainingAmount(deposit.getRemainingAmount().subtract(refundAmount));
        deposit.setPayStatus(2);
        deposit.setRefundTime(LocalDateTime.now());

        if (deposit.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            deposit.setDepositStatus(3);
        }

        return updateById(deposit);
    }

    @Override
    public boolean voidDeposit(Long id, String reason) {
        DepositOrder deposit = getById(id);
        if (deposit == null) {
            throw new BusinessException("定金单不存在");
        }
        deposit.setDepositStatus(0);
        deposit.setRemark(reason);
        return updateById(deposit);
    }

    @Override
    public List<DepositOrder> getValidDeposits(String customerType, Long customerId) {
        LambdaQueryWrapper<DepositOrder> wrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(DepositOrder::getCustomerType, customerType);
        }
        if (customerId != null) {
            wrapper.eq(DepositOrder::getCustomerId, customerId);
        }
        wrapper.eq(DepositOrder::getPayStatus, 1);
        wrapper.eq(DepositOrder::getDepositStatus, 1);
        wrapper.gt(DepositOrder::getRemainingAmount, 0);
        wrapper.orderByDesc(DepositOrder::getCreateTime);
        return list(wrapper);
    }

    @Override
    public BigDecimal getTotalDeposit(String customerType, Long customerId) {
        List<DepositOrder> deposits = getValidDeposits(customerType, customerId);
        return deposits.stream()
                .map(d -> d.getRemainingAmount() != null ? d.getRemainingAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
