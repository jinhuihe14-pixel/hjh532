package com.swim.service.schedule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.schedule.ScheduleRule;

public interface ScheduleRuleService extends IService<ScheduleRule> {

    ScheduleRule getByPositionType(String positionType);
}
