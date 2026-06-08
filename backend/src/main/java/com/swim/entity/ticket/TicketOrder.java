package com.swim.entity.ticket;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ticket_order")
public class TicketOrder extends BaseEntity {

    private String orderNo;

    private Long venueId;

    private String ticketType;

    private LocalDate visitDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private BigDecimal discountAmount;

    private String payType;

    private Integer payStatus;

    private LocalDateTime payTime;

    private Integer orderStatus;

    private String visitorName;

    private String visitorPhone;

    private Long saleId;

    private String remark;
}
