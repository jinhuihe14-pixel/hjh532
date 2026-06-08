package com.swim.service.schedule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.schedule.ScheduleDetail;
import com.swim.mapper.schedule.ScheduleDetailMapper;
import com.swim.service.schedule.ScheduleDetailService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleDetailServiceImpl extends ServiceImpl<ScheduleDetailMapper, ScheduleDetail> implements ScheduleDetailService {

    @Override
    public List<ScheduleDetail> getByPlanId(Long planId) {
        LambdaQueryWrapper<ScheduleDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleDetail::getPlanId, planId);
        wrapper.orderByAsc(ScheduleDetail::getScheduleDate);
        wrapper.orderByAsc(ScheduleDetail::getStartTime);
        return list(wrapper);
    }

    @Override
    public List<ScheduleDetail> getByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ScheduleDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleDetail::getEmployeeId, employeeId);
        wrapper.between(ScheduleDetail::getScheduleDate, startDate, endDate);
        wrapper.orderByAsc(ScheduleDetail::getScheduleDate);
        return list(wrapper);
    }

    @Override
    public List<ScheduleDetail> getByPositionAndDate(String positionType, LocalDate date) {
        LambdaQueryWrapper<ScheduleDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleDetail::getPositionType, positionType);
        wrapper.eq(ScheduleDetail::getScheduleDate, date);
        wrapper.orderByAsc(ScheduleDetail::getStartTime);
        return list(wrapper);
    }

    @Override
    public boolean updateDetail(ScheduleDetail detail) {
        return updateById(detail);
    }
}
