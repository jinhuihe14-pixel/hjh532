package com.swim.service.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.finance.DepositOrder;

import java.math.BigDecimal;
import java.util.List;

public interface DepositOrderService extends IService<DepositOrder> {

    Page<DepositOrder> getDepositPage(PageQuery query, String customerType, Long customerId,
                                       String businessType, Integer depositStatus, Integer payStatus);

    DepositOrder createDeposit(DepositOrder deposit);

    boolean payDeposit(Long id, String payType);

    boolean deductDeposit(Long id, BigDecimal deductAmount, String businessType, Long businessId, String businessNo);

    boolean refundDeposit(Long id, BigDecimal refundAmount, String reason);

    boolean voidDeposit(Long id, String reason);

    List<DepositOrder> getValidDeposits(String customerType, Long customerId);

    BigDecimal getTotalDeposit(String customerType, Long customerId);
}
