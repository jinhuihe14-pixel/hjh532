package com.swim.service.schedule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.schedule.HolidayConfig;

import java.time.LocalDate;
import java.util.List;

public interface HolidayConfigService extends IService<HolidayConfig> {

    List<HolidayConfig> getByYear(Integer year);

    boolean isHoliday(LocalDate date);

    void initYearHolidays(Integer year);
}
