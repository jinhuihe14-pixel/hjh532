package com.swim.service.schedule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.schedule.ScheduleDetail;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleDetailService extends IService<ScheduleDetail> {

    List<ScheduleDetail> getByPlanId(Long planId);

    List<ScheduleDetail> getByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);

    List<ScheduleDetail> getByPositionAndDate(String positionType, LocalDate date);

    boolean updateDetail(ScheduleDetail detail);
}
