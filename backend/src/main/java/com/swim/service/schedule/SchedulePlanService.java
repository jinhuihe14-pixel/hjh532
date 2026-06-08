package com.swim.service.schedule;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.schedule.SchedulePlan;

import java.time.LocalDate;
import java.util.List;

public interface SchedulePlanService extends IService<SchedulePlan> {

    Page<SchedulePlan> getPlanPage(PageQuery query, String positionType, Integer planStatus);

    SchedulePlan generateSchedule(String positionType, LocalDate startDate, LocalDate endDate, String scheduleType);

    boolean publishPlan(Long planId);

    boolean cancelPlan(Long planId);

    List<SchedulePlan> getActivePlans(String positionType, LocalDate date);
}
