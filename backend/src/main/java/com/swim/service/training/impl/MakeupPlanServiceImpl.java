package com.swim.service.training.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.training.MakeupPlan;
import com.swim.mapper.training.MakeupPlanMapper;
import com.swim.service.training.MakeupPlanService;
import org.springframework.stereotype.Service;

@Service
public class MakeupPlanServiceImpl extends ServiceImpl<MakeupPlanMapper, MakeupPlan> implements MakeupPlanService {
}
