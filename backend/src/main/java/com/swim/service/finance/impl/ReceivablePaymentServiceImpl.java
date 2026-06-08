package com.swim.service.finance.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.finance.ReceivableBill;
import com.swim.entity.finance.ReceivablePayment;
import com.swim.mapper.finance.ReceivablePaymentMapper;
import com.swim.service.finance.ReceivableBillService;
import com.swim.service.finance.ReceivablePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceivablePaymentServiceImpl extends ServiceImpl<ReceivablePaymentMapper, ReceivablePayment> implements ReceivablePaymentService {

    private final ReceivableBillService receivableBillService;

    @Override
    public Page<ReceivablePayment> getPaymentPage(PageQuery query, Long billId, Long customerId,
                                                   Integer paymentStatus, String paymentType,
                                                   LocalDate startDate, LocalDate endDate) {
        Page<ReceivablePayment> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ReceivablePayment> wrapper = new LambdaQueryWrapper<>();
        if (billId != null) {
            wrapper.eq(ReceivablePayment::getBillId, billId);
        }
        if (customerId != null) {
            wrapper.eq(ReceivablePayment::getCustomerId, customerId);
        }
        if (paymentStatus != null) {
            wrapper.eq(ReceivablePayment::getPaymentStatus, paymentStatus);
        }
        if (paymentType != null && !paymentType.isEmpty()) {
            wrapper.eq(ReceivablePayment::getPaymentType, paymentType);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(ReceivablePayment::getPaymentDate, startDate, endDate);
        }
        wrapper.orderByDesc(ReceivablePayment::getPaymentDate);
        wrapper.orderByDesc(ReceivablePayment::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReceivablePayment createPayment(ReceivablePayment payment) {
        ReceivableBill bill = receivableBillService.getById(payment.getBillId());
        if (bill == null) {
            throw new BusinessException("应收账单不存在");
        }

        payment.setPaymentNo("RP" + IdUtil.getSnowflakeNextIdStr());
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(1);
        }
        if (payment.getCustomerId() == null) {
            payment.setCustomerId(bill.getCustomerId());
            payment.setCustomerName(bill.getCustomerName());
        }

        BigDecimal unpaid = bill.getUnpaidAmount() != null ? bill.getUnpaidAmount() : BigDecimal.ZERO;
        payment.setIsPartial(payment.getPaymentAmount().compareTo(unpaid) < 0 ? 1 : 0);

        save(payment);

        BigDecimal newPaid = bill.getPaidAmount().add(payment.getPaymentAmount());
        BigDecimal newUnpaid = bill.getReceivableAmount().subtract(newPaid);
        bill.setPaidAmount(newPaid);
        bill.setUnpaidAmount(newUnpaid);

        if (newUnpaid.compareTo(BigDecimal.ZERO) <= 0) {
            bill.setBillStatus(3);
        } else if (newPaid.compareTo(BigDecimal.ZERO) > 0) {
            bill.setBillStatus(2);
        }

        receivableBillService.updateById(bill);

        return payment;
    }

    @Override
    public boolean confirmPayment(Long id) {
        ReceivablePayment payment = getById(id);
        if (payment == null) {
            throw new BusinessException("收款记录不存在");
        }
        if (payment.getPaymentStatus() != 0) {
            throw new BusinessException("只有待确认状态的收款可以确认");
        }
        payment.setPaymentStatus(1);
        payment.setConfirmTime(LocalDateTime.now());
        return updateById(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundPayment(Long id, BigDecimal refundAmount, String remark) {
        ReceivablePayment payment = getById(id);
        if (payment == null) {
            throw new BusinessException("收款记录不存在");
        }
        if (payment.getPaymentStatus() != 1) {
            throw new BusinessException("只有已确认的收款可以退款");
        }
        if (refundAmount.compareTo(payment.getPaymentAmount()) > 0) {
            throw new BusinessException("退款金额不能大于收款金额");
        }

        payment.setPaymentStatus(2);
        payment.setRemark(remark);
        updateById(payment);

        ReceivableBill bill = receivableBillService.getById(payment.getBillId());
        if (bill != null) {
            BigDecimal newPaid = bill.getPaidAmount().subtract(refundAmount);
            BigDecimal newUnpaid = bill.getReceivableAmount().subtract(newPaid);
            bill.setPaidAmount(newPaid);
            bill.setUnpaidAmount(newUnpaid);
            if (newUnpaid.compareTo(BigDecimal.ZERO) > 0) {
                bill.setBillStatus(newPaid.compareTo(BigDecimal.ZERO) > 0 ? 2 : 1);
            }
            receivableBillService.updateById(bill);
        }

        return true;
    }

    @Override
    public List<ReceivablePayment> getByBillId(Long billId) {
        LambdaQueryWrapper<ReceivablePayment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReceivablePayment::getBillId, billId);
        wrapper.orderByDesc(ReceivablePayment::getPaymentDate);
        return list(wrapper);
    }

    @Override
    public BigDecimal getTotalPaid(String customerType, Long customerId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ReceivablePayment> wrapper = new LambdaQueryWrapper<>();
        if (customerId != null) {
            wrapper.eq(ReceivablePayment::getCustomerId, customerId);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(ReceivablePayment::getPaymentDate, startDate, endDate);
        }
        wrapper.eq(ReceivablePayment::getPaymentStatus, 1);
        List<ReceivablePayment> payments = list(wrapper);
        return payments.stream()
                .map(p -> p.getPaymentAmount() != null ? p.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
