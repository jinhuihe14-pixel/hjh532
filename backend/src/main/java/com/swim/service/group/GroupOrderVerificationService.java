package com.swim.service.group;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupOrderVerification;

import java.time.LocalDate;
import java.util.List;

public interface GroupOrderVerificationService extends IService<GroupOrderVerification> {

    Page<GroupOrderVerification> getVerificationPage(PageQuery query, Long orderId, Long venueId, LocalDate startDate, LocalDate endDate);

    GroupOrderVerification verify(Long orderId, Long venueId, LocalDate verifyDate,
                                   java.time.LocalTime startTime, java.time.LocalTime endTime,
                                   Integer actualPeople, Integer usedCount);

    boolean cancelVerification(Long id, String reason);

    List<GroupOrderVerification> getByOrderId(Long orderId);
}
