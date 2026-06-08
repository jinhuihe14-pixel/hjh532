package com.swim.service.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.finance.ReceivableBill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReceivableBillService extends IService<ReceivableBill> {

    Page<ReceivableBill> getBillPage(PageQuery query, String customerType, Long customerId,
                                      Integer billStatus, String billType, LocalDate startDate, LocalDate endDate);

    ReceivableBill createBill(ReceivableBill bill);

    boolean updateBillStatus(Long id, Integer status);

    boolean remind(Long id);

    List<ReceivableBill> getOverdueBills();

    BigDecimal getTotalReceivable(String customerType, Long customerId);

    void generatePeriodBills(String settlementCycle, String period);

    ReceivableBill getByBusiness(String businessType, Long businessId);
}
