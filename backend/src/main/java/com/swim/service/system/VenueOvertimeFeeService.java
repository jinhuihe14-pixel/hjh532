package com.swim.service.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.system.VenueOvertimeFee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VenueOvertimeFeeService extends IService<VenueOvertimeFee> {

    Page<VenueOvertimeFee> getFeePage(PageQuery query, Long rentalOrderId, Long venueId,
                                       Integer payStatus, Integer feeStatus,
                                       LocalDateTime startTime, LocalDateTime endTime);

    VenueOvertimeFee calculateOvertimeFee(Long rentalOrderId, Long venueId, String venueName,
                                           String customerName, String customerPhone,
                                           LocalDateTime scheduledEndTime, LocalDateTime actualEndTime,
                                           String rentalType);

    boolean payFee(Long id, String payType, Long deductionAccountId, Long operatorId, String operatorName);

    boolean waiveFee(Long id, String reason, Long operatorId, String operatorName);

    List<VenueOvertimeFee> getByRentalOrder(Long rentalOrderId);

    BigDecimal calculateAmount(Long venueId, String venueType, String rentalType,
                                LocalDateTime scheduledEndTime, LocalDateTime actualEndTime);
}
