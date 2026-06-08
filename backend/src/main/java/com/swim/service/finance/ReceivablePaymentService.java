package com.swim.service.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.finance.ReceivablePayment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReceivablePaymentService extends IService<ReceivablePayment> {

    Page<ReceivablePayment> getPaymentPage(PageQuery query, Long billId, Long customerId,
                                            Integer paymentStatus, String paymentType,
                                            LocalDate startDate, LocalDate endDate);

    ReceivablePayment createPayment(ReceivablePayment payment);

    boolean confirmPayment(Long id);

    boolean refundPayment(Long id, BigDecimal refundAmount, String remark);

    List<ReceivablePayment> getByBillId(Long billId);

    BigDecimal getTotalPaid(String customerType, Long customerId, LocalDate startDate, LocalDate endDate);
}
