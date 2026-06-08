package com.swim.service.schedule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.schedule.ShiftTemplate;

import java.util.List;

public interface ShiftTemplateService extends IService<ShiftTemplate> {

    List<ShiftTemplate> getActiveList();

    ShiftTemplate getByCode(String shiftCode);
}
