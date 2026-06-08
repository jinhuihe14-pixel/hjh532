package com.swim.mapper.training;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.training.StudentAttendance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentAttendanceMapper extends BaseMapper<StudentAttendance> {
}
