package com.swim.service.schedule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.schedule.HolidayConfig;
import com.swim.mapper.schedule.HolidayConfigMapper;
import com.swim.service.schedule.HolidayConfigService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayConfigServiceImpl extends ServiceImpl<HolidayConfigMapper, HolidayConfig> implements HolidayConfigService {

    @Override
    public List<HolidayConfig> getByYear(Integer year) {
        LambdaQueryWrapper<HolidayConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HolidayConfig::getYear, year);
        wrapper.orderByAsc(HolidayConfig::getHolidayDate);
        return list(wrapper);
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        LambdaQueryWrapper<HolidayConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HolidayConfig::getHolidayDate, date);
        wrapper.eq(HolidayConfig::getHolidayType, "LEGAL");
        return count(wrapper) > 0;
    }

    @Override
    public void initYearHolidays(Integer year) {
    }
}
