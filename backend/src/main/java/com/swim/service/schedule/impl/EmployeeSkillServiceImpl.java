package com.swim.service.schedule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.schedule.EmployeeSkill;
import com.swim.mapper.schedule.EmployeeSkillMapper;
import com.swim.service.schedule.EmployeeSkillService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeSkillServiceImpl extends ServiceImpl<EmployeeSkillMapper, EmployeeSkill> implements EmployeeSkillService {

    @Override
    public List<EmployeeSkill> getByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<EmployeeSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeSkill::getEmployeeId, employeeId);
        return list(wrapper);
    }

    @Override
    public List<EmployeeSkill> getBySkillCode(String skillCode) {
        LambdaQueryWrapper<EmployeeSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeSkill::getSkillCode, skillCode);
        return list(wrapper);
    }
}
