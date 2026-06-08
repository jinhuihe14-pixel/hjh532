package com.swim.service.system.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.system.VenueOvertimeFee;
import com.swim.entity.system.VenueOvertimeRule;
import com.swim.mapper.system.VenueOvertimeFeeMapper;
import com.swim.service.system.VenueOvertimeFeeService;
import com.swim.service.system.VenueOvertimeRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueOvertimeFeeServiceImpl extends ServiceImpl<VenueOvertimeFeeMapper, VenueOvertimeFee> implements VenueOvertimeFeeService {

    private final VenueOvertimeRuleService overtimeRuleService;

    @Override
    public Page<VenueOvertimeFee> getFeePage(PageQuery query, Long rentalOrderId, Long venueId,
                                              Integer payStatus, Integer feeStatus,
                                              LocalDateTime startTime, LocalDateTime endTime) {
        Page<VenueOvertimeFee> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<VenueOvertimeFee> wrapper = new LambdaQueryWrapper<>();
        if (rentalOrderId != null) {
            wrapper.eq(VenueOvertimeFee::getRentalOrderId, rentalOrderId);
        }
        if (venueId != null) {
            wrapper.eq(VenueOvertimeFee::getVenueId, venueId);
        }
        if (payStatus != null) {
            wrapper.eq(VenueOvertimeFee::getPayStatus, payStatus);
        }
        if (feeStatus != null) {
            wrapper.eq(VenueOvertimeFee::getFeeStatus, feeStatus);
        }
        if (startTime != null && endTime != null) {
            wrapper.between(VenueOvertimeFee::getCreateTime, startTime, endTime);
        }
        wrapper.orderByDesc(VenueOvertimeFee::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VenueOvertimeFee calculateOvertimeFee(Long rentalOrderId, Long venueId, String venueName,
                                                  String customerName, String customerPhone,
                                                  LocalDateTime scheduledEndTime, LocalDateTime actualEndTime,
                                                  String rentalType) {
        if (!actualEndTime.isAfter(scheduledEndTime)) {
            throw new BusinessException("未超时，无需计算超时费用");
        }

        long totalMinutes = Duration.between(scheduledEndTime, actualEndTime).toMinutes();

        VenueOvertimeRule rule = overtimeRuleService.getMatchRule(venueId, null, rentalType);
        if (rule == null) {
            throw new BusinessException("未找到匹配的超时计费规则");
        }

        int graceMinutes = rule.getGraceMinutes() != null ? rule.getGraceMinutes() : 0;
        if (totalMinutes <= graceMinutes) {
            return null;
        }

        long chargeableMinutes = totalMinutes - graceMinutes;
        int billingUnit = rule.getBillingUnit() != null ? rule.getBillingUnit() : 30;
        int billingUnits = (int) Math.ceil((double) chargeableMinutes / billingUnit);

        BigDecimal unitPrice = rule.getOvertimeRate();
        BigDecimal overtimeAmount = unitPrice.multiply(BigDecimal.valueOf(billingUnits));

        if (rule.getMaxOvertimeAmount() != null && overtimeAmount.compareTo(rule.getMaxOvertimeAmount()) > 0) {
            overtimeAmount = rule.getMaxOvertimeAmount();
        }
        if (rule.getMaxOvertimeMinutes() != null && totalMinutes > rule.getMaxOvertimeMinutes()) {
            chargeableMinutes = rule.getMaxOvertimeMinutes() - graceMinutes;
            billingUnits = (int) Math.ceil((double) chargeableMinutes / billingUnit);
            overtimeAmount = unitPrice.multiply(BigDecimal.valueOf(billingUnits));
        }

        VenueOvertimeFee fee = new VenueOvertimeFee();
        fee.setFeeNo("VOF" + IdUtil.getSnowflakeNextIdStr());
        fee.setRentalOrderId(rentalOrderId);
        fee.setVenueId(venueId);
        fee.setVenueName(venueName);
        fee.setCustomerName(customerName);
        fee.setCustomerPhone(customerPhone);
        fee.setScheduledEndTime(scheduledEndTime);
        fee.setActualEndTime(actualEndTime);
        fee.setOvertimeMinutes((int) totalMinutes);
        fee.setBillingUnits(billingUnits);
        fee.setUnitPrice(unitPrice);
        fee.setOvertimeAmount(overtimeAmount);
        fee.setGraceMinutes(graceMinutes);
        fee.setRuleId(rule.getId());
        fee.setPayStatus(0);
        fee.setFeeStatus(1);
        fee.setOperatorId(null);

        save(fee);
        return fee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payFee(Long id, String payType, Long deductionAccountId, Long operatorId, String operatorName) {
        VenueOvertimeFee fee = getById(id);
        if (fee == null) {
            throw new BusinessException("超时费用记录不存在");
        }
        if (fee.getPayStatus() != 0 || fee.getFeeStatus() != 1) {
            throw new BusinessException("该费用不可支付");
        }

        fee.setPayStatus(1);
        fee.setPayTime(LocalDateTime.now());
        fee.setPayType(payType);
        fee.setDeductionAccountId(deductionAccountId);
        fee.setOperatorId(operatorId);
        fee.setOperatorName(operatorName);
        fee.setFeeStatus(2);

        return updateById(fee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean waiveFee(Long id, String reason, Long operatorId, String operatorName) {
        VenueOvertimeFee fee = getById(id);
        if (fee == null) {
            throw new BusinessException("超时费用记录不存在");
        }
        if (fee.getFeeStatus() != 1) {
            throw new BusinessException("该费用不可减免");
        }

        fee.setPayStatus(2);
        fee.setFeeStatus(3);
        fee.setWaiverReason(reason);
        fee.setOperatorId(operatorId);
        fee.setOperatorName(operatorName);

        return updateById(fee);
    }

    @Override
    public List<VenueOvertimeFee> getByRentalOrder(Long rentalOrderId) {
        LambdaQueryWrapper<VenueOvertimeFee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VenueOvertimeFee::getRentalOrderId, rentalOrderId);
        wrapper.orderByDesc(VenueOvertimeFee::getCreateTime);
        return list(wrapper);
    }

    @Override
    public BigDecimal calculateAmount(Long venueId, String venueType, String rentalType,
                                       LocalDateTime scheduledEndTime, LocalDateTime actualEndTime) {
        if (!actualEndTime.isAfter(scheduledEndTime)) {
            return BigDecimal.ZERO;
        }

        long totalMinutes = Duration.between(scheduledEndTime, actualEndTime).toMinutes();
        VenueOvertimeRule rule = overtimeRuleService.getMatchRule(venueId, venueType, rentalType);
        if (rule == null) {
            return BigDecimal.ZERO;
        }

        int graceMinutes = rule.getGraceMinutes() != null ? rule.getGraceMinutes() : 0;
        if (totalMinutes <= graceMinutes) {
            return BigDecimal.ZERO;
        }

        long chargeableMinutes = totalMinutes - graceMinutes;
        int billingUnit = rule.getBillingUnit() != null ? rule.getBillingUnit() : 30;
        int billingUnits = (int) Math.ceil((double) chargeableMinutes / billingUnit);

        BigDecimal overtimeAmount = rule.getOvertimeRate().multiply(BigDecimal.valueOf(billingUnits));

        if (rule.getMaxOvertimeAmount() != null && overtimeAmount.compareTo(rule.getMaxOvertimeAmount()) > 0) {
            overtimeAmount = rule.getMaxOvertimeAmount();
        }

        return overtimeAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
