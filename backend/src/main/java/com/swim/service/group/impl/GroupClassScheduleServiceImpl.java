package com.swim.service.group.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupClass;
import com.swim.entity.group.GroupClassSchedule;
import com.swim.mapper.group.GroupClassScheduleMapper;
import com.swim.service.group.GroupClassScheduleService;
import com.swim.service.group.GroupClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupClassScheduleServiceImpl extends ServiceImpl<GroupClassScheduleMapper, GroupClassSchedule> implements GroupClassScheduleService {

    private final GroupClassService groupClassService;

    @Override
    public Page<GroupClassSchedule> getSchedulePage(PageQuery query, Long classId, Long coachId, Long venueId,
                                                     LocalDate startDate, LocalDate endDate) {
        Page<GroupClassSchedule> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GroupClassSchedule> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(GroupClassSchedule::getClassId, classId);
        }
        if (coachId != null) {
            wrapper.eq(GroupClassSchedule::getCoachId, coachId);
        }
        if (venueId != null) {
            wrapper.eq(GroupClassSchedule::getVenueId, venueId);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(GroupClassSchedule::getClassDate, startDate, endDate);
        }
        wrapper.orderByAsc(GroupClassSchedule::getClassDate);
        wrapper.orderByAsc(GroupClassSchedule::getStartTime);
        return page(page, wrapper);
    }

    @Override
    public List<GroupClassSchedule> getByClassId(Long classId) {
        LambdaQueryWrapper<GroupClassSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupClassSchedule::getClassId, classId);
        wrapper.orderByAsc(GroupClassSchedule::getClassDate);
        return list(wrapper);
    }

    @Override
    public GroupClassSchedule createSchedule(GroupClassSchedule schedule) {
        schedule.setScheduleNo("GCS" + IdUtil.getSnowflakeNextIdStr());
        schedule.setScheduleStatus(1);
        if (schedule.getActualStudentCount() == null) {
            schedule.setActualStudentCount(0);
        }
        save(schedule);
        return schedule;
    }

    @Override
    public boolean cancelSchedule(Long id) {
        GroupClassSchedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课不存在");
        }
        schedule.setScheduleStatus(0);
        return updateById(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeSchedule(Long id, Integer actualStudentCount, String teachingContent) {
        GroupClassSchedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课不存在");
        }
        if (schedule.getScheduleStatus() != 1) {
            throw new BusinessException("只有待上课状态可以完成");
        }

        schedule.setScheduleStatus(2);
        schedule.setActualStudentCount(actualStudentCount);
        schedule.setTeachingContent(teachingContent);

        boolean result = updateById(schedule);

        if (result) {
            GroupClass groupClass = groupClassService.getById(schedule.getClassId());
            if (groupClass != null) {
                int completed = groupClass.getCompletedSessions() == null ? 0 : groupClass.getCompletedSessions();
                groupClass.setCompletedSessions(completed + 1);

                if (groupClass.getTotalSessions() != null && groupClass.getCompletedSessions() >= groupClass.getTotalSessions()) {
                    groupClass.setClassStatus(3);
                }
                groupClassService.updateById(groupClass);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<GroupClassSchedule> generateSchedules(Long classId) {
        GroupClass groupClass = groupClassService.getById(classId);
        if (groupClass == null) {
            throw new BusinessException("团体课程不存在");
        }
        if (groupClass.getClassStartDate() == null || groupClass.getClassEndDate() == null) {
            throw new BusinessException("请先设置课程起止日期");
        }
        if (groupClass.getClassWeekDay() == null || groupClass.getClassWeekDay().isEmpty()) {
            throw new BusinessException("请先设置上课星期");
        }

        List<Integer> weekDays = Arrays.stream(groupClass.getClassWeekDay().split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<GroupClassSchedule> schedules = new ArrayList<>();
        LocalDate currentDate = groupClass.getClassStartDate();
        LocalDate endDate = groupClass.getClassEndDate();
        int sessionCount = 0;

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            int dayValue = dayOfWeek.getValue();

            if (weekDays.contains(dayValue)) {
                GroupClassSchedule schedule = new GroupClassSchedule();
                schedule.setScheduleNo("GCS" + IdUtil.getSnowflakeNextIdStr());
                schedule.setClassId(classId);
                schedule.setClassName(groupClass.getClassName());
                schedule.setCustomerId(groupClass.getCustomerId());
                schedule.setCoachId(groupClass.getCoachId());
                schedule.setCoachName(groupClass.getCoachName());
                schedule.setVenueId(groupClass.getVenueId());
                schedule.setVenueName(groupClass.getVenueName());
                schedule.setClassDate(currentDate);
                schedule.setStartTime(groupClass.getClassStartTime());
                schedule.setEndTime(groupClass.getClassEndTime());
                schedule.setClassHours(groupClass.getTotalSessions() != null ?
                        groupClass.getTotalSessions().doubleValue() > 0 ?
                                java.math.BigDecimal.valueOf(groupClass.getTotalSessions()) : null : null);
                schedule.setPlanStudentCount(groupClass.getStudentCount());
                schedule.setScheduleStatus(1);

                schedules.add(schedule);
                sessionCount++;
            }

            currentDate = currentDate.plusDays(1);
        }

        if (!schedules.isEmpty()) {
            saveBatch(schedules);
            groupClass.setTotalSessions(sessionCount);
            groupClassService.updateById(groupClass);
        }

        return schedules;
    }
}
