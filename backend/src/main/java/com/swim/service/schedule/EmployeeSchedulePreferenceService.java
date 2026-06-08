package com.swim.service.schedule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.schedule.EmployeeSchedulePreference;

public interface EmployeeSchedulePreferenceService extends IService<EmployeeSchedulePreference> {

    EmployeeSchedulePreference getByEmployeeId(Long employeeId);
}
