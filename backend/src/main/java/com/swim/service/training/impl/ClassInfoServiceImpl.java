package com.swim.service.training.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.entity.training.*;
import com.swim.mapper.training.*;
import com.swim.service.resource.VenueOccupationService;
import com.swim.service.training.ClassInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ClassInfoServiceImpl extends ServiceImpl<ClassInfoMapper, ClassInfo>
        implements ClassInfoService {

    private final StudentMapper studentMapper;
    private final ClassStudentMapper classStudentMapper;
    private final HoursLogMapper hoursLogMapper;
    private final VenueOccupationService venueOccupationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassInfo createClass(ClassInfo classInfo) {
        classInfo.setClassNo("CLS" + IdUtil.getSnowflakeNextIdStr().substring(0, 10));
        if (classInfo.getClassStatus() == null) {
            classInfo.setClassStatus(1);
        }
        if (classInfo.getStudentCount() == null) {
            classInfo.setStudentCount(0);
        }
        if (classInfo.getCompletedHours() == null) {
            classInfo.setCompletedHours(BigDecimal.ZERO);
        }
        if (classInfo.getRemainingHours() == null) {
            classInfo.setRemainingHours(classInfo.getTotalHours());
        }
        save(classInfo);
        return classInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enrollStudent(Long classId, Long studentId, BigDecimal hours) {
        ClassInfo classInfo = getById(classId);
        if (classInfo == null) {
            throw new BusinessException("班级不存在");
        }
        if (classInfo.getClassStatus() != 1 && classInfo.getClassStatus() != 2) {
            throw new BusinessException("班级状态不允许报名");
        }
        if (classInfo.getMaxStudent() != null && classInfo.getStudentCount() >= classInfo.getMaxStudent()) {
            throw new BusinessException("班级人数已满");
        }

        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }

        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId)
                .ne(ClassStudent::getStudentStatus, 0);
        Long count = classStudentMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该学员已在本班级");
        }

        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(classId);
        classStudent.setStudentId(studentId);
        classStudent.setStudentName(student.getStudentName());
        classStudent.setEnrollTime(LocalDateTime.now());
        classStudent.setEnrollmentType("NORMAL");
        classStudent.setEnrolledHours(hours);
        classStudent.setConsumedHours(BigDecimal.ZERO);
        classStudent.setRemainingHours(hours);
        classStudent.setStudentStatus(1);
        classStudentMapper.insert(classStudent);

        classInfo.setStudentCount(classInfo.getStudentCount() + 1);
        updateById(classInfo);

        recordHoursLog(studentId, classId, null, null, "TRAINING", "BUY",
                hours, BigDecimal.ZERO, hours, null, null, "报名课时", null);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferStudent(Long fromClassId, Long toClassId, Long studentId, BigDecimal hours) {
        ClassInfo fromClass = getById(fromClassId);
        ClassInfo toClass = getById(toClassId);
        if (fromClass == null || toClass == null) {
            throw new BusinessException("班级不存在");
        }

        LambdaQueryWrapper<ClassStudent> fromWrapper = new LambdaQueryWrapper<>();
        fromWrapper.eq(ClassStudent::getClassId, fromClassId)
                .eq(ClassStudent::getStudentId, studentId)
                .eq(ClassStudent::getStudentStatus, 1);
        ClassStudent fromStudent = classStudentMapper.selectOne(fromWrapper);
        if (fromStudent == null) {
            throw new BusinessException("学员不在原班级或状态异常");
        }

        if (fromStudent.getRemainingHours().compareTo(hours) < 0) {
            throw new BusinessException("剩余课时不足");
        }

        if (toClass.getMaxStudent() != null && toClass.getStudentCount() >= toClass.getMaxStudent()) {
            throw new BusinessException("目标班级人数已满");
        }

        fromStudent.setRemainingHours(fromStudent.getRemainingHours().subtract(hours));
        if (fromStudent.getRemainingHours().compareTo(BigDecimal.ZERO) <= 0) {
            fromStudent.setStudentStatus(0);
        }
        classStudentMapper.updateById(fromStudent);

        boolean hasTarget = false;
        LambdaQueryWrapper<ClassStudent> toWrapper = new LambdaQueryWrapper<>();
        toWrapper.eq(ClassStudent::getClassId, toClassId)
                .eq(ClassStudent::getStudentId, studentId);
        ClassStudent toStudent = classStudentMapper.selectOne(toWrapper);
        if (toStudent != null) {
            if (toStudent.getStudentStatus() == 0) {
                toStudent.setStudentStatus(1);
            }
            toStudent.setRemainingHours(toStudent.getRemainingHours().add(hours));
            toStudent.setEnrolledHours(toStudent.getEnrolledHours().add(hours));
            classStudentMapper.updateById(toStudent);
            hasTarget = true;
        }

        if (!hasTarget) {
            Student student = studentMapper.selectById(studentId);
            ClassStudent newStudent = new ClassStudent();
            newStudent.setClassId(toClassId);
            newStudent.setStudentId(studentId);
            newStudent.setStudentName(student.getStudentName());
            newStudent.setEnrollTime(LocalDateTime.now());
            newStudent.setEnrollmentType("TRANSFER");
            newStudent.setEnrolledHours(hours);
            newStudent.setConsumedHours(BigDecimal.ZERO);
            newStudent.setRemainingHours(hours);
            newStudent.setStudentStatus(1);
            newStudent.setSourceClassId(fromClassId);
            classStudentMapper.insert(newStudent);

            toClass.setStudentCount(toClass.getStudentCount() + 1);
            updateById(toClass);
        }

        if (fromStudent.getRemainingHours().compareTo(BigDecimal.ZERO) <= 0) {
            fromClass.setStudentCount(fromClass.getStudentCount() - 1);
            updateById(fromClass);
        }

        recordHoursLog(studentId, fromClassId, null, null, "TRAINING",
                "TRANSFER_OUT", hours.negate(),
                fromStudent.getRemainingHours().add(hours), fromStudent.getRemainingHours(),
                null, null, "转班转出-" + toClass.getClassName(), null);

        recordHoursLog(studentId, toClassId, null, null, "TRAINING",
                "TRANSFER_IN", hours,
                hasTarget ? toStudent.getRemainingHours().subtract(hours) : BigDecimal.ZERO,
                hasTarget ? toStudent.getRemainingHours() : hours,
                null, null, "转班转入-" + fromClass.getClassName(), null);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean suspendStudent(Long classId, Long studentId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId)
                .eq(ClassStudent::getStudentStatus, 1);
        ClassStudent classStudent = classStudentMapper.selectOne(wrapper);
        if (classStudent == null) {
            throw new BusinessException("学员不在该班级或状态异常");
        }

        classStudent.setStudentStatus(2);
        classStudent.setSuspensionStart(startDate);
        classStudent.setSuspensionEnd(endDate);
        classStudentMapper.updateById(classStudent);

        ClassInfo classInfo = getById(classId);
        classInfo.setStudentCount(classInfo.getStudentCount() - 1);
        updateById(classInfo);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resumeStudent(Long classId, Long studentId) {
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId)
                .eq(ClassStudent::getStudentStatus, 2);
        ClassStudent classStudent = classStudentMapper.selectOne(wrapper);
        if (classStudent == null) {
            throw new BusinessException("学员不在休学状态");
        }

        ClassInfo classInfo = getById(classId);
        if (classInfo.getMaxStudent() != null && classInfo.getStudentCount() >= classInfo.getMaxStudent()) {
            throw new BusinessException("班级人数已满，无法复学");
        }

        classStudent.setStudentStatus(1);
        classStudent.setSuspensionStart(null);
        classStudent.setSuspensionEnd(null);
        classStudentMapper.updateById(classStudent);

        classInfo.setStudentCount(classInfo.getStudentCount() + 1);
        updateById(classInfo);

        return true;
    }

    private void recordHoursLog(Long studentId, Long classId, Long scheduleId, Long cardId,
                                String hoursType, String changeType, BigDecimal changeHours,
                                BigDecimal beforeHours, BigDecimal afterHours,
                                String businessType, Long businessId, String operatorName, String remark) {
        HoursLog log = new HoursLog();
        log.setLogNo("HL" + IdUtil.getSnowflakeNextIdStr());
        log.setStudentId(studentId);
        log.setClassId(classId);
        log.setScheduleId(scheduleId);
        log.setCardId(cardId);
        log.setHoursType(hoursType);
        log.setChangeType(changeType);
        log.setChangeHours(changeHours);
        log.setBeforeHours(beforeHours);
        log.setAfterHours(afterHours);
        log.setBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setOperatorName(operatorName);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        hoursLogMapper.insert(log);
    }
}
