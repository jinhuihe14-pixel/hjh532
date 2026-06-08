package com.swim.service.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.finance.PrepaymentAccountLog;

import java.time.LocalDateTime;
import java.util.List;

public interface PrepaymentAccountLogService extends IService<PrepaymentAccountLog> {

    Page<PrepaymentAccountLog> getLogPage(PageQuery query, Long accountId, String changeType,
                                           String businessType, LocalDateTime startTime, LocalDateTime endTime);

    List<PrepaymentAccountLog> getByAccountId(Long accountId);

    List<PrepaymentAccountLog> getByBusiness(String businessType, Long businessId);

    void addLog(Long accountId, String accountNo, Long customerId, String customerName,
                String accountType, String balanceType, String changeType, java.math.BigDecimal changeAmount,
                java.math.BigDecimal beforeBalance, java.math.BigDecimal afterBalance,
                String businessType, Long businessId, String businessNo,
                Long operatorId, String operatorName, String remark);
}
