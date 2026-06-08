package com.swim.service.group;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupOrder;

import java.util.List;

public interface GroupOrderService extends IService<GroupOrder> {

    Page<GroupOrder> getOrderPage(PageQuery query, Long customerId, Integer orderStatus, Integer payStatus, String orderType);

    GroupOrder getByNo(String orderNo);

    GroupOrder createOrder(GroupOrder order);

    boolean confirmOrder(Long id);

    boolean cancelOrder(Long id);

    boolean finishOrder(Long id);

    List<GroupOrder> getValidOrders(Long customerId);

    boolean lockVenue(Long orderId);

    boolean unlockVenue(Long orderId);
}
