package com.swim.service.finance.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.finance.ReceivableBill;
import com.swim.entity.finance.ReceivableSettlement;
import com.swim.mapper.finance.ReceivableSettlementMapper;
import com.swim.service.finance.ReceivableBillService;
import com.swim.service.finance.ReceivableSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceivableSettlementServiceImpl extends ServiceImpl<ReceivableSettlementMapper, ReceivableSettlement> implements ReceivableSettlementService {

    private final ReceivableBillService receivableBillService;

    @Override
    public Page<ReceivableSettlement> getSettlementPage(PageQuery query, String customerType, Long customerId,
                                                         String settlementPeriod, Integer settlementStatus) {
        Page<ReceivableSettlement> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ReceivableSettlement> wrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(ReceivableSettlement::getCustomerType, customerType);
        }
        if (customerId != null) {
            wrapper.eq(ReceivableSettlement::getCustomerId, customerId);
        }
        if (settlementPeriod != null && !settlementPeriod.isEmpty()) {
            wrapper.eq(ReceivableSettlement::getSettlementPeriod, settlementPeriod);
        }
        if (settlementStatus != null) {
            wrapper.eq(ReceivableSettlement::getSettlementStatus, settlementStatus);
        }
        wrapper.orderByDesc(ReceivableSettlement::getSettlementPeriod);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReceivableSettlement generateSettlement(String customerType, Long customerId, String settlementPeriod) {
        YearMonth yearMonth = YearMonth.parse(settlementPeriod, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate periodStart = yearMonth.atDay(1);
        LocalDate periodEnd = yearMonth.atEndOfMonth();

        ReceivableSettlement exist = getByPeriod(customerType, customerId, settlementPeriod);
        if (exist != null) {
            throw new BusinessException("该周期对账已存在");
        }

        LambdaQueryWrapper<ReceivableBill> billWrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            billWrapper.eq(ReceivableBill::getCustomerType, customerType);
        }
        if (customerId != null) {
            billWrapper.eq(ReceivableBill::getCustomerId, customerId);
        }
        billWrapper.le(ReceivableBill::getBillDate, periodEnd);
        List<ReceivableBill> allBills = receivableBillService.list(billWrapper);

        LambdaQueryWrapper<ReceivableBill> periodWrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            periodWrapper.eq(ReceivableBill::getCustomerType, customerType);
        }
        if (customerId != null) {
            periodWrapper.eq(ReceivableBill::getCustomerId, customerId);
        }
        periodWrapper.between(ReceivableBill::getBillDate, periodStart, periodEnd);
        List<ReceivableBill> periodBills = receivableBillService.list(periodWrapper);

        BigDecimal openingBalance = BigDecimal.ZERO;
        BigDecimal periodAddAmount = BigDecimal.ZERO;
        BigDecimal periodReceivedAmount = BigDecimal.ZERO;
        int billCount = periodBills.size();
        int paidBillCount = 0;
        int unpaidBillCount = 0;
        int overdueBillCount = 0;
        BigDecimal overdueAmount = BigDecimal.ZERO;

        for (ReceivableBill bill : allBills) {
            if (bill.getBillDate().isBefore(periodStart)) {
                BigDecimal unpaid = bill.getUnpaidAmount() != null ? bill.getUnpaidAmount() : BigDecimal.ZERO;
                if (unpaid.compareTo(BigDecimal.ZERO) > 0) {
                    openingBalance = openingBalance.add(unpaid);
                }
            }
        }

        for (ReceivableBill bill : periodBills) {
            BigDecimal receivable = bill.getReceivableAmount() != null ? bill.getReceivableAmount() : BigDecimal.ZERO;
            periodAddAmount = periodAddAmount.add(receivable);

            if (bill.getBillStatus() != null && bill.getBillStatus() == 3) {
                paidBillCount++;
                periodReceivedAmount = periodReceivedAmount.add(bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO);
            } else {
                unpaidBillCount++;
                if (bill.getDueDate() != null && bill.getDueDate().isBefore(periodEnd)) {
                    overdueBillCount++;
                    BigDecimal unpaid = bill.getUnpaidAmount() != null ? bill.getUnpaidAmount() : BigDecimal.ZERO;
                    overdueAmount = overdueAmount.add(unpaid);
                }
            }
        }

        BigDecimal closingBalance = openingBalance.add(periodAddAmount).subtract(periodReceivedAmount);

        ReceivableSettlement settlement = new ReceivableSettlement();
        settlement.setSettlementNo("RS" + IdUtil.getSnowflakeNextIdStr());
        settlement.setCustomerType(customerType);
        settlement.setCustomerId(customerId);

        LambdaQueryWrapper<ReceivableBill> nameWrapper = new LambdaQueryWrapper<>();
        if (customerId != null) {
            nameWrapper.eq(ReceivableBill::getCustomerId, customerId);
            nameWrapper.last("limit 1");
            ReceivableBill oneBill = receivableBillService.getOne(nameWrapper);
            if (oneBill != null) {
                settlement.setCustomerName(oneBill.getCustomerName());
            }
        }

        settlement.setSettlementPeriod(settlementPeriod);
        settlement.setPeriodStart(periodStart);
        settlement.setPeriodEnd(periodEnd);
        settlement.setOpeningBalance(openingBalance);
        settlement.setPeriodAddAmount(periodAddAmount);
        settlement.setPeriodReceivedAmount(periodReceivedAmount);
        settlement.setClosingBalance(closingBalance);
        settlement.setBillCount(billCount);
        settlement.setPaidBillCount(paidBillCount);
        settlement.setUnpaidBillCount(unpaidBillCount);
        settlement.setOverdueBillCount(overdueBillCount);
        settlement.setOverdueAmount(overdueAmount);
        settlement.setSettlementStatus(0);

        save(settlement);
        return settlement;
    }

    @Override
    public boolean confirmSettlement(Long id) {
        ReceivableSettlement settlement = getById(id);
        if (settlement == null) {
            throw new BusinessException("对账单不存在");
        }
        settlement.setSettlementStatus(1);
        return updateById(settlement);
    }

    @Override
    public boolean objectSettlement(Long id, String remark) {
        ReceivableSettlement settlement = getById(id);
        if (settlement == null) {
            throw new BusinessException("对账单不存在");
        }
        settlement.setSettlementStatus(2);
        settlement.setRemark(remark);
        return updateById(settlement);
    }

    @Override
    public List<ReceivableSettlement> getByCustomer(String customerType, Long customerId) {
        LambdaQueryWrapper<ReceivableSettlement> wrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(ReceivableSettlement::getCustomerType, customerType);
        }
        if (customerId != null) {
            wrapper.eq(ReceivableSettlement::getCustomerId, customerId);
        }
        wrapper.orderByDesc(ReceivableSettlement::getSettlementPeriod);
        return list(wrapper);
    }

    @Override
    public ReceivableSettlement getByPeriod(String customerType, Long customerId, String settlementPeriod) {
        LambdaQueryWrapper<ReceivableSettlement> wrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(ReceivableSettlement::getCustomerType, customerType);
        }
        if (customerId != null) {
            wrapper.eq(ReceivableSettlement::getCustomerId, customerId);
        }
        wrapper.eq(ReceivableSettlement::getSettlementPeriod, settlementPeriod);
        return getOne(wrapper);
    }
}
