package com.swim.service.training.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.swim.common.BusinessException;
import com.swim.entity.training.*;
import com.swim.mapper.training.*;
import com.swim.service.training.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final StudentAttendanceMapper attendanceMapper;
    private final ClassScheduleMapper scheduleMapper;
    private final ClassStudentMapper classStudentMapper;
    private final HoursLogMapper hoursLogMapper;
    private final MakeupPlanMapper makeupPlanMapper;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordAttendance(Long scheduleId, Long studentId, Integer status, String remark) {
        ClassSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("排课不存在");
        }

        LambdaQueryWrapper<StudentAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAttendance::getScheduleId, scheduleId)
                .eq(StudentAttendance::getStudentId, studentId);
        StudentAttendance attendance = attendanceMapper.selectOne(wrapper);

        if (attendance == null) {
            Student student = studentMapper.selectById(studentId);
            attendance = new StudentAttendance();
            attendance.setScheduleId(scheduleId);
            attendance.setClassId(schedule.getClassId());
            attendance.setStudentId(studentId);
            attendance.setStudentName(student != null ? student.getStudentName() : "");
            attendance.setNeedMakeup(0);
            attendance.setMakeupStatus(0);
            attendance.setCreateTime(LocalDateTime.now());
        }

        attendance.setAttendanceStatus(status);
        attendance.setRemark(remark);

        boolean isAbsent = status == 2;
        BigDecimal hours = schedule.getClassHours();

        if (status == 1 || status == 4 || status == 5) {
            attendance.setHoursConsumed(hours);
            consumeClassHours(schedule.getClassId(), studentId, hours, scheduleId);
            attendance.setNeedMakeup(0);
            attendance.setMakeupStatus(0);
        } else if (isAbsent) {
            attendance.setHoursConsumed(BigDecimal.ZERO);
            attendance.setNeedMakeup(1);
            attendance.setMakeupStatus(1);
            generateMakeupPlan(schedule, studentId);
        } else if (status == 3) {
            attendance.setHoursConsumed(BigDecimal.ZERO);
            attendance.setNeedMakeup(0);
            attendance.setMakeupStatus(0);
        }

        if (attendance.getId() == null) {
            attendanceMapper.insert(attendance);
        } else {
            attendance.setUpdateTime(LocalDateTime.now());
            attendanceMapper.updateById(attendance);
        }

        updateScheduleAttendanceCount(scheduleId);

        return true;
    }

    @Override
    public List<StudentAttendance> getAttendanceBySchedule(Long scheduleId) {
        LambdaQueryWrapper<StudentAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAttendance::getScheduleId, scheduleId);
        wrapper.orderByAsc(StudentAttendance::getId);
        return attendanceMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRecordAttendance(Long scheduleId, List<StudentAttendance> attendanceList) {
        for (StudentAttendance attendance : attendanceList) {
            recordAttendance(scheduleId, attendance.getStudentId(),
                    attendance.getAttendanceStatus(), attendance.getRemark());
        }
        return true;
    }

    private void consumeClassHours(Long classId, Long studentId, BigDecimal hours, Long scheduleId) {
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId);
        ClassStudent classStudent = classStudentMapper.selectOne(wrapper);
        if (classStudent == null) {
            return;
        }

        BigDecimal beforeHours = classStudent.getRemainingHours();
        classStudent.setConsumedHours(classStudent.getConsumedHours().add(hours));
        classStudent.setRemainingHours(classStudent.getRemainingHours().subtract(hours));
        classStudent.setUpdateTime(LocalDateTime.now());
        classStudentMapper.updateById(classStudent);

        recordHoursLog(studentId, classId, scheduleId, null, "TRAINING", "CONSUME",
                hours.negate(), beforeHours, classStudent.getRemainingHours(),
                null, null, "上课扣课时", null);
    }

    private void generateMakeupPlan(ClassSchedule schedule, Long studentId) {
        Student student = studentMapper.selectById(studentId);

        MakeupPlan plan = new MakeupPlan();
        plan.setPlanNo("MK" + IdUtil.getSnowflakeNextIdStr());
        plan.setStudentId(studentId);
        plan.setStudentName(student != null ? student.getStudentName() : "");
        plan.setOriginalScheduleId(schedule.getId());
        plan.setOriginalClassId(schedule.getClassId());
        plan.setMakeupStatus(1);
        plan.setDeadline(schedule.getClassDate().plusDays(30));
        plan.setCreateTime(LocalDateTime.now());
        plan.setRemark("缺课自动生成补课计划");

        makeupPlanMapper.insert(plan);

        log.info("自动生成补课计划: studentId={}, scheduleId={}, planNo={}",
                studentId, schedule.getId(), plan.getPlanNo());
    }

    private void updateScheduleAttendanceCount(Long scheduleId) {
        List<StudentAttendance> list = getAttendanceBySchedule(scheduleId);
        int attendanceCount = 0;
        int absentCount = 0;
        int leaveCount = 0;

        for (StudentAttendance a : list) {
            if (a.getAttendanceStatus() == null) continue;
            switch (a.getAttendanceStatus()) {
                case 1, 4, 5 -> attendanceCount++;
                case 2 -> absentCount++;
                case 3 -> leaveCount++;
            }
        }

        ClassSchedule schedule = new ClassSchedule();
        schedule.setId(scheduleId);
        schedule.setAttendanceCount(attendanceCount);
        schedule.setAbsentCount(absentCount);
        schedule.setLeaveCount(leaveCount);
        schedule.setUpdateTime(LocalDateTime.now());
        scheduleMapper.updateById(schedule);
    }

    private void recordHoursLog(Long studentId, Long classId, Long scheduleId, Long cardId,
                                String hoursType, String changeType, BigDecimal changeHours,
                                BigDecimal beforeHours, BigDecimal afterHours,
                                String businessType, Long businessId, String operatorName, String remark) {
        HoursLog logEntry = new HoursLog();
        logEntry.setLogNo("HL" + IdUtil.getSnowflakeNextIdStr());
        logEntry.setStudentId(studentId);
        logEntry.setClassId(classId);
        logEntry.setScheduleId(scheduleId);
        logEntry.setCardId(cardId);
        logEntry.setHoursType(hoursType);
        logEntry.setChangeType(changeType);
        logEntry.setChangeHours(changeHours);
        logEntry.setBeforeHours(beforeHours);
        logEntry.setAfterHours(afterHours);
        logEntry.setBusinessType(businessType);
        logEntry.setBusinessId(businessId);
        logEntry.setOperatorName(operatorName);
        logEntry.setRemark(remark);
        logEntry.setCreateTime(LocalDateTime.now());
        hoursLogMapper.insert(logEntry);
    }
}
