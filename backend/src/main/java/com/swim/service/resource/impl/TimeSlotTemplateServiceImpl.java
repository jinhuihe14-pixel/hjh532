package com.swim.service.resource.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.resource.TimeSlotTemplate;
import com.swim.mapper.resource.TimeSlotTemplateMapper;
import com.swim.service.resource.TimeSlotTemplateService;
import org.springframework.stereotype.Service;

@Service
public class TimeSlotTemplateServiceImpl extends ServiceImpl<TimeSlotTemplateMapper, TimeSlotTemplate>
        implements TimeSlotTemplateService {
}
