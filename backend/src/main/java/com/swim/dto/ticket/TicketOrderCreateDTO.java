package com.swim.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TicketOrderCreateDTO {

    @NotNull(message = "场地ID不能为空")
    private Long venueId;

    @NotBlank(message = "票种不能为空")
    private String ticketType;

    @NotNull(message = "入场日期不能为空")
    private LocalDate visitDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    private String visitorName;

    private String visitorPhone;

    private String payType;

    private String remark;
}
