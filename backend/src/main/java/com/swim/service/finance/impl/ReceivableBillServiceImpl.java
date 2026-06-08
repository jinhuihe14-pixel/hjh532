package com.swim.service.finance.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.finance.ReceivableBill;
import com.swim.mapper.finance.ReceivableBillMapper;
import com.swim.service.finance.ReceivableBillService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReceivableBillServiceImpl extends ServiceImpl<ReceivableBillMapper, ReceivableBill> implements ReceivableBillService {

    @Override
    public Page<ReceivableBill> getBillPage(PageQuery query, String customerType, Long customerId,
                                             Integer billStatus, String billType, LocalDate startDate, LocalDate endDate) {
        Page<ReceivableBill> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ReceivableBill> wrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(ReceivableBill::getCustomerType, customerType);
        }
        if (customerId != null) {
            wrapper.eq(ReceivableBill::getCustomerId, customerId);
        }
        if (billStatus != null) {
            wrapper.eq(ReceivableBill::getBillStatus, billStatus);
        }
        if (billType != null && !billType.isEmpty()) {
            wrapper.eq(ReceivableBill::getBillType, billType);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(ReceivableBill::getBillDate, startDate, endDate);
        }
        wrapper.orderByDesc(ReceivableBill::getBillDate);
        wrapper.orderByDesc(ReceivableBill::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public ReceivableBill createBill(ReceivableBill bill) {
        bill.setBillNo("RB" + IdUtil.getSnowflakeNextIdStr());
        bill.setBillStatus(1);
        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(BigDecimal.ZERO);
        }
        if (bill.getUnpaidAmount() == null) {
            bill.setUnpaidAmount(bill.getReceivableAmount());
        }
        if (bill.getRemindCount() == null) {
            bill.setRemindCount(0);
        }
        if (bill.getBillDate() == null) {
            bill.setBillDate(LocalDate.now());
        }
        if (bill.getDueDate() == null && bill.getAccountPeriod() != null) {
            bill.setDueDate(bill.getBillDate().plusDays(bill.getAccountPeriod()));
        }
        save(bill);
        return bill;
    }

    @Override
    public boolean updateBillStatus(Long id, Integer status) {
        ReceivableBill bill = getById(id);
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }
        bill.setBillStatus(status);
        return updateById(bill);
    }

    @Override
    public boolean remind(Long id) {
        ReceivableBill bill = getById(id);
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }
        bill.setRemindCount(bill.getRemindCount() + 1);
        bill.setLastRemindTime(LocalDateTime.now());
        return updateById(bill);
    }

    @Override
    public List<ReceivableBill> getOverdueBills() {
        LambdaQueryWrapper<ReceivableBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(ReceivableBill::getDueDate, LocalDate.now());
        wrapper.in(ReceivableBill::getBillStatus, 1, 2);
        wrapper.eq(ReceivableBill::getDeleted, 0);
        return list(wrapper);
    }

    @Override
    public BigDecimal getTotalReceivable(String customerType, Long customerId) {
        LambdaQueryWrapper<ReceivableBill> wrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(ReceivableBill::getCustomerType, customerType);
        }
        if (customerId != null) {
            wrapper.eq(ReceivableBill::getCustomerId, customerId);
        }
        wrapper.in(ReceivableBill::getBillStatus, 1, 2, 4);
        List<ReceivableBill> bills = list(wrapper);
        return bills.stream()
                .map(b -> b.getUnpaidAmount() != null ? b.getUnpaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void generatePeriodBills(String settlementCycle, String period) {
    }

    @Override
    public ReceivableBill getByBusiness(String businessType, Long businessId) {
        LambdaQueryWrapper<ReceivableBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReceivableBill::getBusinessType, businessType);
        wrapper.eq(ReceivableBill::getBusinessId, businessId);
        return getOne(wrapper);
    }
}
