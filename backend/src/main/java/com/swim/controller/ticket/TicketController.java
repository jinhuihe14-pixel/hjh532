package com.swim.controller.ticket;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swim.common.PageQuery;
import com.swim.common.Result;
import com.swim.dto.ticket.TicketOrderCreateDTO;
import com.swim.entity.ticket.TicketOrder;
import com.swim.service.ticket.TicketOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@Tag(name = "散客票管理")
public class TicketController {

    private final TicketOrderService ticketOrderService;

    @GetMapping("/order/page")
    public Result<Page<TicketOrder>> getOrderPage(PageQuery query, Integer orderStatus, Integer payStatus,
                                                  String visitorName, String phone) {
        Page<TicketOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        if (orderStatus != null) {
            wrapper.eq(TicketOrder::getOrderStatus, orderStatus);
        }
        if (payStatus != null) {
            wrapper.eq(TicketOrder::getPayStatus, payStatus);
        }
        if (visitorName != null && !visitorName.isEmpty()) {
            wrapper.like(TicketOrder::getVisitorName, visitorName);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(TicketOrder::getVisitorPhone, phone);
        }
        wrapper.orderByDesc(TicketOrder::getCreateTime);
        return Result.success(ticketOrderService.page(page, wrapper));
    }

    @GetMapping("/order/{id}")
    public Result<TicketOrder> getOrder(@PathVariable Long id) {
        return Result.success(ticketOrderService.getById(id));
    }

    @PostMapping("/order")
    public Result<TicketOrder> createOrder(@Valid @RequestBody TicketOrderCreateDTO dto) {
        return Result.success(ticketOrderService.createOrder(dto));
    }

    @PostMapping("/order/{id}/pay")
    public Result<TicketOrder> payOrder(@PathVariable Long id, @RequestParam String payType) {
        return Result.success(ticketOrderService.payOrder(id, payType));
    }

    @PostMapping("/order/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        ticketOrderService.cancelOrder(id);
        return Result.success();
    }
}
