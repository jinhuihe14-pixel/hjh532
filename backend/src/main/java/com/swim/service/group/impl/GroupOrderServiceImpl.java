package com.swim.service.group.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupOrder;
import com.swim.mapper.group.GroupOrderMapper;
import com.swim.service.group.GroupOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupOrderServiceImpl extends ServiceImpl<GroupOrderMapper, GroupOrder> implements GroupOrderService {

    @Override
    public Page<GroupOrder> getOrderPage(PageQuery query, Long customerId, Integer orderStatus, Integer payStatus, String orderType) {
        Page<GroupOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GroupOrder> wrapper = new LambdaQueryWrapper<>();
        if (customerId != null) {
            wrapper.eq(GroupOrder::getCustomerId, customerId);
        }
        if (orderStatus != null) {
            wrapper.eq(GroupOrder::getOrderStatus, orderStatus);
        }
        if (payStatus != null) {
            wrapper.eq(GroupOrder::getPayStatus, payStatus);
        }
        if (orderType != null && !orderType.isEmpty()) {
            wrapper.eq(GroupOrder::getOrderType, orderType);
        }
        wrapper.orderByDesc(GroupOrder::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public GroupOrder getByNo(String orderNo) {
        LambdaQueryWrapper<GroupOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupOrder::getOrderNo, orderNo);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GroupOrder createOrder(GroupOrder order) {
        order.setOrderNo("GO" + IdUtil.getSnowflakeNextIdStr());
        order.setOrderStatus(1);
        order.setPayStatus(0);
        order.setUsedVisits(0);
        if (order.getRemainingVisits() == null) {
            order.setRemainingVisits(order.getEstimatedVisits());
        }

        if (order.getTotalAmount() == null) {
            order.setTotalAmount(BigDecimal.ZERO);
        }
        if (order.getDiscountAmount() == null) {
            order.setDiscountAmount(BigDecimal.ZERO);
        }
        if (order.getPayableAmount() == null) {
            order.setPayableAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
        }
        if (order.getPaidAmount() == null) {
            order.setPaidAmount(BigDecimal.ZERO);
        }
        if (order.getUnpaidAmount() == null) {
            order.setUnpaidAmount(order.getPayableAmount().subtract(order.getPaidAmount()));
        }
        if (order.getDepositAmount() == null) {
            order.setDepositAmount(BigDecimal.ZERO);
        }

        save(order);
        return order;
    }

    @Override
    public boolean confirmOrder(Long id) {
        GroupOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("团单不存在");
        }
        if (order.getOrderStatus() != 1) {
            throw new BusinessException("只有待确认状态的订单可以确认");
        }
        order.setOrderStatus(2);
        order.setConfirmTime(LocalDateTime.now());

        if (order.getIsVenueLocked() != null && order.getIsVenueLocked() == 1) {
        }

        return updateById(order);
    }

    @Override
    public boolean cancelOrder(Long id) {
        GroupOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("团单不存在");
        }
        order.setOrderStatus(0);
        return updateById(order);
    }

    @Override
    public boolean finishOrder(Long id) {
        GroupOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("团单不存在");
        }
        order.setOrderStatus(3);
        order.setFinishTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    public List<GroupOrder> getValidOrders(Long customerId) {
        LambdaQueryWrapper<GroupOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupOrder::getCustomerId, customerId);
        wrapper.in(GroupOrder::getOrderStatus, 1, 2);
        wrapper.orderByDesc(GroupOrder::getCreateTime);
        return list(wrapper);
    }

    @Override
    public boolean lockVenue(Long orderId) {
        GroupOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("团单不存在");
        }
        order.setIsVenueLocked(1);
        return updateById(order);
    }

    @Override
    public boolean unlockVenue(Long orderId) {
        GroupOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("团单不存在");
        }
        order.setIsVenueLocked(0);
        return updateById(order);
    }
}
