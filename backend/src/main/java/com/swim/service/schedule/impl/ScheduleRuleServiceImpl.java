package com.swim.service.schedule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.schedule.ScheduleRule;
import com.swim.mapper.schedule.ScheduleRuleMapper;
import com.swim.service.schedule.ScheduleRuleService;
import org.springframework.stereotype.Service;

@Service
public class ScheduleRuleServiceImpl extends ServiceImpl<ScheduleRuleMapper, ScheduleRule> implements ScheduleRuleService {

    @Override
    public ScheduleRule getByPositionType(String positionType) {
        LambdaQueryWrapper<ScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleRule::getPositionType, positionType);
        wrapper.eq(ScheduleRule::getStatus, 1);
        return getOne(wrapper);
    }
}
