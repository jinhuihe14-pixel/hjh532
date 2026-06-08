package com.swim.service.schedule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.schedule.ShiftTemplate;
import com.swim.mapper.schedule.ShiftTemplateMapper;
import com.swim.service.schedule.ShiftTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShiftTemplateServiceImpl extends ServiceImpl<ShiftTemplateMapper, ShiftTemplate> implements ShiftTemplateService {

    @Override
    public List<ShiftTemplate> getActiveList() {
        LambdaQueryWrapper<ShiftTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShiftTemplate::getStatus, 1);
        wrapper.orderByAsc(ShiftTemplate::getSort);
        return list(wrapper);
    }

    @Override
    public ShiftTemplate getByCode(String shiftCode) {
        LambdaQueryWrapper<ShiftTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShiftTemplate::getShiftCode, shiftCode);
        return getOne(wrapper);
    }
}
