package com.swim.service.schedule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.schedule.EmployeeSkill;

import java.util.List;

public interface EmployeeSkillService extends IService<EmployeeSkill> {

    List<EmployeeSkill> getByEmployeeId(Long employeeId);

    List<EmployeeSkill> getBySkillCode(String skillCode);
}
