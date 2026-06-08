package com.swim.service.training.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.entity.resource.VenueOccupation;
import com.swim.entity.training.ClassInfo;
import com.swim.entity.training.ClassSchedule;
import com.swim.mapper.training.ClassScheduleMapper;
import com.swim.service.resource.VenueOccupationService;
import com.swim.service.training.ClassScheduleService;
import com.swim.service.training.ClassInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassScheduleServiceImpl extends ServiceImpl<ClassScheduleMapper, ClassSchedule>
        implements ClassScheduleService {

    private final ClassInfoService classInfoService;
    private final VenueOccupationService venueOccupationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ClassSchedule> generateSchedules(Long classId) {
        ClassInfo classInfo = classInfoService.getById(classId);
        if (classInfo == null) {
            throw new BusinessException("班级不存在");
        }
        if (classInfo.getWeekDay() == null || classInfo.getWeekDay().isEmpty()) {
            throw new BusinessException("请先设置上课星期");
        }

        List<ClassSchedule> schedules = new ArrayList<>();
        String[] weekDays = classInfo.getWeekDay().split(",");
        LocalDate startDate = classInfo.getClassDateStart();
        LocalDate endDate = classInfo.getClassDateEnd();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            int dayValue = dayOfWeek.getValue();

            for (String weekDay : weekDays) {
                if (Integer.parseInt(weekDay.trim()) == dayValue) {
                    ClassSchedule schedule = new ClassSchedule();
                    schedule.setScheduleNo("SCH" + IdUtil.getSnowflakeNextIdStr());
                    schedule.setClassId(classId);
                    schedule.setCourseId(classInfo.getCourseId());
                    schedule.setCoachId(classInfo.getCoachId());
                    schedule.setVenueId(classInfo.getVenueId());
                    schedule.setClassDate(date);
                    schedule.setStartTime(classInfo.getClassStartTime());
                    schedule.setEndTime(classInfo.getClassEndTime());
                    schedule.setClassHours(classInfo.getTotalHours() != null ?
                            classInfo.getTotalHours().divide(java.math.BigDecimal.valueOf(
                                    calculateTotalWeeks(startDate, endDate, weekDays)
                            ), 2, java.math.RoundingMode.HALF_UP) : java.math.BigDecimal.ZERO);
                    schedule.setScheduleStatus(1);
                    schedule.setIsMakeup(0);
                    schedule.setAttendanceCount(0);
                    schedule.setAbsentCount(0);
                    schedule.setLeaveCount(0);

                    boolean conflict = venueOccupationService.checkConflict(
                            classInfo.getVenueId(), date,
                            classInfo.getClassStartTime(), classInfo.getClassEndTime(), null
                    );
                    if (conflict) {
                        throw new BusinessException("排课冲突：" + date + " " +
                                classInfo.getClassStartTime() + "-" + classInfo.getClassEndTime() +
                                " 场地已被占用");
                    }

                    save(schedule);

                    VenueOccupation occupation = new VenueOccupation();
                    occupation.setVenueId(classInfo.getVenueId());
                    occupation.setOccupationDate(date);
                    occupation.setStartTime(classInfo.getClassStartTime());
                    occupation.setEndTime(classInfo.getClassEndTime());
                    occupation.setUsageType("TRAINING");
                    occupation.setBusinessType("TRAINING_CLASS");
                    occupation.setBusinessId(schedule.getId());
                    occupation.setBusinessNo(schedule.getScheduleNo());
                    occupation.setLockStatus(0);
                    occupation.setStatus(1);
                    venueOccupationService.addOccupation(occupation);

                    schedules.add(schedule);
                }
            }
        }

        return schedules;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSchedule(Long scheduleId) {
        ClassSchedule schedule = getById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("排课不存在");
        }
        if (schedule.getScheduleStatus() == 2) {
            throw new BusinessException("已完成的课程不能取消");
        }

        schedule.setScheduleStatus(0);
        updateById(schedule);

        venueOccupationService.cancelOccupation("TRAINING_CLASS", scheduleId);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reschedule(Long scheduleId, LocalDate newDate,
                              LocalTime newStartTime, LocalTime newEndTime) {
        ClassSchedule schedule = getById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("排课不存在");
        }
        if (schedule.getScheduleStatus() == 2) {
            throw new BusinessException("已完成的课程不能改期");
        }

        venueOccupationService.updateOccupation("TRAINING_CLASS", scheduleId,
                newDate, newStartTime, newEndTime);

        schedule.setClassDate(newDate);
        schedule.setStartTime(newStartTime);
        schedule.setEndTime(newEndTime);
        updateById(schedule);

        return true;
    }

    private int calculateTotalWeeks(LocalDate start, LocalDate end, String[] weekDays) {
        int count = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int dayValue = date.getDayOfWeek().getValue();
            for (String weekDay : weekDays) {
                if (Integer.parseInt(weekDay.trim()) == dayValue) {
                    count++;
                    break;
                }
            }
        }
        return Math.max(count, 1);
    }
}
