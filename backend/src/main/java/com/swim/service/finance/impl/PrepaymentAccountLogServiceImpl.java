package com.swim.service.finance.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.PageQuery;
import com.swim.entity.finance.PrepaymentAccountLog;
import com.swim.mapper.finance.PrepaymentAccountLogMapper;
import com.swim.service.finance.PrepaymentAccountLogService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrepaymentAccountLogServiceImpl extends ServiceImpl<PrepaymentAccountLogMapper, PrepaymentAccountLog> implements PrepaymentAccountLogService {

    @Override
    public Page<PrepaymentAccountLog> getLogPage(PageQuery query, Long accountId, String changeType,
                                                  String businessType, LocalDateTime startTime, LocalDateTime endTime) {
        Page<PrepaymentAccountLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PrepaymentAccountLog> wrapper = new LambdaQueryWrapper<>();
        if (accountId != null) {
            wrapper.eq(PrepaymentAccountLog::getAccountId, accountId);
        }
        if (changeType != null && !changeType.isEmpty()) {
            wrapper.eq(PrepaymentAccountLog::getChangeType, changeType);
        }
        if (businessType != null && !businessType.isEmpty()) {
            wrapper.eq(PrepaymentAccountLog::getBusinessType, businessType);
        }
        if (startTime != null && endTime != null) {
            wrapper.between(PrepaymentAccountLog::getCreateTime, startTime, endTime);
        }
        wrapper.orderByDesc(PrepaymentAccountLog::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public List<PrepaymentAccountLog> getByAccountId(Long accountId) {
        LambdaQueryWrapper<PrepaymentAccountLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrepaymentAccountLog::getAccountId, accountId);
        wrapper.orderByDesc(PrepaymentAccountLog::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<PrepaymentAccountLog> getByBusiness(String businessType, Long businessId) {
        LambdaQueryWrapper<PrepaymentAccountLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrepaymentAccountLog::getBusinessType, businessType);
        wrapper.eq(PrepaymentAccountLog::getBusinessId, businessId);
        return list(wrapper);
    }

    @Override
    public void addLog(Long accountId, String accountNo, Long customerId, String customerName,
                       String accountType, String balanceType, String changeType, BigDecimal changeAmount,
                       BigDecimal beforeBalance, BigDecimal afterBalance,
                       String businessType, Long businessId, String businessNo,
                       Long operatorId, String operatorName, String remark) {
        PrepaymentAccountLog log = new PrepaymentAccountLog();
        log.setLogNo("PAL" + IdUtil.getSnowflakeNextIdStr());
        log.setAccountId(accountId);
        log.setAccountNo(accountNo);
        log.setCustomerId(customerId);
        log.setCustomerName(customerName);
        log.setAccountType(accountType);
        log.setBalanceType(balanceType);
        log.setChangeType(changeType);
        log.setChangeAmount(changeAmount);
        log.setBeforeBalance(beforeBalance);
        log.setAfterBalance(afterBalance);
        log.setBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setBusinessNo(businessNo);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        save(log);
    }
}
