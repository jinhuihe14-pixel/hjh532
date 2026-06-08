package com.swim.service.schedule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.schedule.EmployeeSchedulePreference;
import com.swim.mapper.schedule.EmployeeSchedulePreferenceMapper;
import com.swim.service.schedule.EmployeeSchedulePreferenceService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeSchedulePreferenceServiceImpl extends ServiceImpl<EmployeeSchedulePreferenceMapper, EmployeeSchedulePreference> implements EmployeeSchedulePreferenceService {

    @Override
    public EmployeeSchedulePreference getByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<EmployeeSchedulePreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeSchedulePreference::getEmployeeId, employeeId);
        return getOne(wrapper);
    }
}
