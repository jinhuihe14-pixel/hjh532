package com.swim.service.ticket.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.dto.ticket.TicketOrderCreateDTO;
import com.swim.entity.resource.VenueOccupation;
import com.swim.entity.ticket.TicketOrder;
import com.swim.mapper.ticket.TicketOrderMapper;
import com.swim.service.resource.VenueOccupationService;
import com.swim.service.ticket.TicketOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketOrderServiceImpl extends ServiceImpl<TicketOrderMapper, TicketOrder>
        implements TicketOrderService {

    private final VenueOccupationService venueOccupationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketOrder createOrder(TicketOrderCreateDTO dto) {
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            boolean conflict = venueOccupationService.checkConflict(
                    dto.getVenueId(), dto.getVisitDate(),
                    dto.getStartTime(), dto.getEndTime(), null
            );
            if (conflict) {
                throw new BusinessException("所选时段场地已被占用");
            }
        }

        BigDecimal unitPrice = calculateUnitPrice(dto.getTicketType(), dto.getStartTime(), dto.getEndTime());
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

        TicketOrder order = new TicketOrder();
        order.setOrderNo("TK" + IdUtil.getSnowflakeNextIdStr());
        order.setVenueId(dto.getVenueId());
        order.setTicketType(dto.getTicketType());
        order.setVisitDate(dto.getVisitDate());
        order.setStartTime(dto.getStartTime());
        order.setEndTime(dto.getEndTime());
        order.setQuantity(dto.getQuantity());
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayStatus(0);
        order.setOrderStatus(1);
        order.setVisitorName(dto.getVisitorName());
        order.setVisitorPhone(dto.getVisitorPhone());
        order.setRemark(dto.getRemark());

        save(order);

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketOrder payOrder(Long orderId, String payType) {
        TicketOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getPayStatus() == 1) {
            throw new BusinessException("订单已支付");
        }
        if (order.getOrderStatus() == 0) {
            throw new BusinessException("订单已取消");
        }

        order.setPayStatus(1);
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        order.setOrderStatus(1);

        updateById(order);

        if (order.getStartTime() != null && order.getEndTime() != null) {
            VenueOccupation occupation = new VenueOccupation();
            occupation.setVenueId(order.getVenueId());
            occupation.setOccupationDate(order.getVisitDate());
            occupation.setStartTime(order.getStartTime());
            occupation.setEndTime(order.getEndTime());
            occupation.setUsageType("PUBLIC");
            occupation.setBusinessType("TICKET");
            occupation.setBusinessId(order.getId());
            occupation.setBusinessNo(order.getOrderNo());
            occupation.setLockStatus(0);
            occupation.setStatus(1);
            venueOccupationService.addOccupation(occupation);
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId) {
        TicketOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getOrderStatus() == 0) {
            throw new BusinessException("订单已取消");
        }

        order.setOrderStatus(0);
        boolean result = updateById(order);

        if (order.getPayStatus() == 1 && order.getStartTime() != null) {
            venueOccupationService.cancelOccupation("TICKET", orderId);
        }

        return result;
    }

    private BigDecimal calculateUnitPrice(String ticketType, java.time.LocalTime startTime,
                                          java.time.LocalTime endTime) {
        return switch (ticketType) {
            case "MORNING" -> BigDecimal.valueOf(30);
            case "EVENING" -> BigDecimal.valueOf(50);
            default -> BigDecimal.valueOf(40);
        };
    }
}
