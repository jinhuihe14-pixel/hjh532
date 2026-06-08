package com.swim.service.training;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.training.ClassInfo;

public interface ClassInfoService extends IService<ClassInfo> {

    ClassInfo createClass(ClassInfo classInfo);

    boolean enrollStudent(Long classId, Long studentId, java.math.BigDecimal hours);

    boolean transferStudent(Long fromClassId, Long toClassId, Long studentId, java.math.BigDecimal hours);

    boolean suspendStudent(Long classId, Long studentId, java.time.LocalDate startDate, java.time.LocalDate endDate);

    boolean resumeStudent(Long classId, Long studentId);
}
