package com.swim.service.group.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupOrder;
import com.swim.entity.group.GroupOrderVerification;
import com.swim.mapper.group.GroupOrderVerificationMapper;
import com.swim.service.group.GroupOrderService;
import com.swim.service.group.GroupOrderVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupOrderVerificationServiceImpl extends ServiceImpl<GroupOrderVerificationMapper, GroupOrderVerification> implements GroupOrderVerificationService {

    private final GroupOrderService groupOrderService;

    @Override
    public Page<GroupOrderVerification> getVerificationPage(PageQuery query, Long orderId, Long venueId, LocalDate startDate, LocalDate endDate) {
        Page<GroupOrderVerification> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GroupOrderVerification> wrapper = new LambdaQueryWrapper<>();
        if (orderId != null) {
            wrapper.eq(GroupOrderVerification::getOrderId, orderId);
        }
        if (venueId != null) {
            wrapper.eq(GroupOrderVerification::getVenueId, venueId);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(GroupOrderVerification::getVerifyDate, startDate, endDate);
        }
        wrapper.orderByDesc(GroupOrderVerification::getVerifyDate);
        wrapper.orderByDesc(GroupOrderVerification::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GroupOrderVerification verify(Long orderId, Long venueId, LocalDate verifyDate,
                                          LocalTime startTime, LocalTime endTime,
                                          Integer actualPeople, Integer usedCount) {
        GroupOrder order = groupOrderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("团单不存在");
        }
        if (order.getOrderStatus() != 2) {
            throw new BusinessException("只有进行中的团单可以核销");
        }
        if (order.getRemainingVisits() == null || order.getRemainingVisits() < usedCount) {
            throw new BusinessException("剩余次数不足");
        }

        BigDecimal verifyAmount = BigDecimal.ZERO;
        if (order.getExclusivePrice() != null) {
            verifyAmount = order.getExclusivePrice().multiply(BigDecimal.valueOf(usedCount));
        }

        GroupOrderVerification verification = new GroupOrderVerification();
        verification.setVerificationNo("GOV" + IdUtil.getSnowflakeNextIdStr());
        verification.setOrderId(orderId);
        verification.setOrderNo(order.getOrderNo());
        verification.setCustomerId(order.getCustomerId());
        verification.setCustomerName(order.getCustomerName());
        verification.setVenueId(venueId);
        verification.setVerifyDate(verifyDate);
        verification.setStartTime(startTime);
        verification.setEndTime(endTime);
        verification.setActualPeople(actualPeople);
        verification.setUsedCount(usedCount);
        verification.setUnitPrice(order.getExclusivePrice());
        verification.setVerifyAmount(verifyAmount);
        verification.setVerifyStatus(1);
        verification.setVerifyTime(LocalDateTime.now());

        save(verification);

        order.setUsedVisits(order.getUsedVisits() + usedCount);
        order.setRemainingVisits(order.getRemainingVisits() - usedCount);

        if (order.getRemainingVisits() <= 0) {
            order.setOrderStatus(3);
            order.setFinishTime(LocalDateTime.now());
        }

        groupOrderService.updateById(order);

        return verification;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelVerification(Long id, String reason) {
        GroupOrderVerification verification = getById(id);
        if (verification == null) {
            throw new BusinessException("核销记录不存在");
        }
        if (verification.getVerifyStatus() != 1) {
            throw new BusinessException("只有已核销的记录可以撤销");
        }

        GroupOrder order = groupOrderService.getById(verification.getOrderId());
        if (order != null) {
            order.setUsedVisits(order.getUsedVisits() - verification.getUsedCount());
            order.setRemainingVisits(order.getRemainingVisits() + verification.getUsedCount());
            if (order.getOrderStatus() == 3 && order.getRemainingVisits() > 0) {
                order.setOrderStatus(2);
                order.setFinishTime(null);
            }
            groupOrderService.updateById(order);
        }

        verification.setVerifyStatus(0);
        verification.setCancelTime(LocalDateTime.now());
        verification.setCancelReason(reason);

        return updateById(verification);
    }

    @Override
    public List<GroupOrderVerification> getByOrderId(Long orderId) {
        LambdaQueryWrapper<GroupOrderVerification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupOrderVerification::getOrderId, orderId);
        wrapper.orderByDesc(GroupOrderVerification::getVerifyDate);
        return list(wrapper);
    }
}
