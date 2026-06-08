package com.swim.service.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.finance.PrepaymentAccount;

import java.math.BigDecimal;
import java.util.List;

public interface PrepaymentAccountService extends IService<PrepaymentAccount> {

    Page<PrepaymentAccount> getAccountPage(PageQuery query, String accountType, String customerType,
                                            Long customerId, Integer status, String keyword);

    PrepaymentAccount createAccount(PrepaymentAccount account);

    boolean recharge(Long accountId, BigDecimal amount, BigDecimal giftAmount, String businessType,
                     Long businessId, String businessNo, Long operatorId, String operatorName, String remark);

    boolean consume(Long accountId, BigDecimal amount, String balanceType, String businessType,
                    Long businessId, String businessNo, Long operatorId, String operatorName, String remark);

    boolean refund(Long accountId, BigDecimal amount, String businessType, Long businessId,
                   String businessNo, Long operatorId, String operatorName, String remark);

    boolean freeze(Long accountId, BigDecimal amount, String remark);

    boolean unfreeze(Long accountId, BigDecimal amount, String remark);

    PrepaymentAccount getByCustomer(String customerType, Long customerId, String accountType);

    BigDecimal getTotalBalance(String accountType);
}
