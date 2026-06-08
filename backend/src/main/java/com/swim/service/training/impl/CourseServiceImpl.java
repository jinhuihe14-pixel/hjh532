package com.swim.service.training.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.training.Course;
import com.swim.mapper.training.CourseMapper;
import com.swim.service.training.CourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
}
