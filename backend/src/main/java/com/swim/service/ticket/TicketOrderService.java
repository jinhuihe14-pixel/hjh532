package com.swim.service.ticket;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.dto.ticket.TicketOrderCreateDTO;
import com.swim.entity.ticket.TicketOrder;

public interface TicketOrderService extends IService<TicketOrder> {

    TicketOrder createOrder(TicketOrderCreateDTO dto);

    TicketOrder payOrder(Long orderId, String payType);

    boolean cancelOrder(Long orderId);
}
