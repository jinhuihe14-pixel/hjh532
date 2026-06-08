package com.swim.mapper.training;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.training.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
