package com.swim.service.schedule.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.schedule.*;
import com.swim.mapper.schedule.SchedulePlanMapper;
import com.swim.service.schedule.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SchedulePlanServiceImpl extends ServiceImpl<SchedulePlanMapper, SchedulePlan> implements SchedulePlanService {

    private final ScheduleDetailService scheduleDetailService;
    private final ScheduleRuleService scheduleRuleService;
    private final ShiftTemplateService shiftTemplateService;
    private final EmployeeSkillService employeeSkillService;
    private final EmployeeSchedulePreferenceService employeePreferenceService;
    private final HolidayConfigService holidayConfigService;

    @Override
    public Page<SchedulePlan> getPlanPage(PageQuery query, String positionType, Integer planStatus) {
        Page<SchedulePlan> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SchedulePlan> wrapper = new LambdaQueryWrapper<>();
        if (positionType != null && !positionType.isEmpty()) {
            wrapper.eq(SchedulePlan::getPositionType, positionType);
        }
        if (planStatus != null) {
            wrapper.eq(SchedulePlan::getPlanStatus, planStatus);
        }
        wrapper.orderByDesc(SchedulePlan::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchedulePlan generateSchedule(String positionType, LocalDate startDate, LocalDate endDate, String scheduleType) {
        ScheduleRule rule = scheduleRuleService.getByPositionType(positionType);
        if (rule == null) {
            throw new BusinessException("未找到该岗位的排班规则");
        }

        List<ShiftTemplate> shifts = shiftTemplateService.getActiveList();
        if (shifts.isEmpty()) {
            throw new BusinessException("未找到可用班次模板");
        }

        List<EmployeeSkill> skills = employeeSkillService.getBySkillCode(positionType);
        if (skills.isEmpty()) {
            throw new BusinessException("未找到具备该岗位技能的员工");
        }

        List<Long> employeeIds = new ArrayList<>();
        Map<Long, String> employeeNames = new HashMap<>();
        Map<Long, String> employeeNos = new HashMap<>();
        for (EmployeeSkill skill : skills) {
            employeeIds.add(skill.getEmployeeId());
        }

        SchedulePlan plan = new SchedulePlan();
        plan.setPlanNo("SP" + IdUtil.getSnowflakeNextIdStr());
        plan.setPlanName(positionType + "排班_" + startDate + "_" + endDate);
        plan.setPositionType(positionType);
        plan.setScheduleType(scheduleType);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setPlanStatus(0);
        save(plan);

        List<ScheduleDetail> details = new ArrayList<>();
        Map<Long, Integer> consecutiveDaysMap = new HashMap<>();
        Map<Long, BigDecimal> weeklyHoursMap = new HashMap<>();

        LocalDate currentDate = startDate;
        int dayIndex = 0;
        while (!currentDate.isAfter(endDate)) {
            boolean isWeekend = isWeekend(currentDate);
            boolean isHoliday = holidayConfigService.isHoliday(currentDate);

            int requiredStaff = rule.getMinDailyStaff();
            if (isWeekend && rule.getWeekendStaffBoost() != null) {
                requiredStaff += rule.getWeekendStaffBoost();
            }
            if (isHoliday && rule.getHolidayStaffBoost() != null) {
                requiredStaff += rule.getHolidayStaffBoost();
            }

            List<Long> todayEmployees = selectEmployeesForDay(
                    employeeIds, consecutiveDaysMap, weeklyHoursMap,
                    rule, currentDate, requiredStaff, shifts.size()
            );

            int shiftIndex = 0;
            for (Long empId : todayEmployees) {
                ShiftTemplate shift = shifts.get(shiftIndex % shifts.size());
                shiftIndex++;

                ScheduleDetail detail = new ScheduleDetail();
                detail.setPlanId(plan.getId());
                detail.setEmployeeId(empId);
                detail.setEmployeeName(employeeNames.getOrDefault(empId, "员工" + empId));
                detail.setEmployeeNo(employeeNos.getOrDefault(empId, ""));
                detail.setPositionType(positionType);
                detail.setShiftId(shift.getId());
                detail.setShiftCode(shift.getShiftCode());
                detail.setShiftName(shift.getShiftName());
                detail.setScheduleDate(currentDate);
                detail.setStartTime(shift.getStartTime());
                detail.setEndTime(shift.getEndTime());
                detail.setWorkHours(shift.getWorkHours());
                detail.setIsWeekend(isWeekend ? 1 : 0);
                detail.setIsHoliday(isHoliday ? 1 : 0);
                detail.setIsOvertime(0);
                detail.setScheduleSource("AUTO");
                details.add(detail);

                consecutiveDaysMap.put(empId, consecutiveDaysMap.getOrDefault(empId, 0) + 1);
                weeklyHoursMap.put(empId, weeklyHoursMap.getOrDefault(empId, BigDecimal.ZERO).add(shift.getWorkHours()));
            }

            for (Long empId : employeeIds) {
                if (!todayEmployees.contains(empId)) {
                    consecutiveDaysMap.put(empId, 0);
                }
            }

            currentDate = currentDate.plusDays(1);
            dayIndex++;
        }

        if (!details.isEmpty()) {
            scheduleDetailService.saveBatch(details);
            plan.setTotalShifts(details.size());
            plan.setTotalStaff((int) details.stream().map(ScheduleDetail::getEmployeeId).distinct().count());
            updateById(plan);
        }

        return plan;
    }

    private List<Long> selectEmployeesForDay(List<Long> employeeIds, Map<Long, Integer> consecutiveDaysMap,
                                             Map<Long, BigDecimal> weeklyHoursMap, ScheduleRule rule,
                                             LocalDate date, int requiredCount, int shiftCount) {
        List<Long> result = new ArrayList<>();
        List<Long> candidates = new ArrayList<>(employeeIds);

        Collections.shuffle(candidates);

        candidates.sort((a, b) -> {
            int daysA = consecutiveDaysMap.getOrDefault(a, 0);
            int daysB = consecutiveDaysMap.getOrDefault(b, 0);
            return Integer.compare(daysA, daysB);
        });

        for (Long empId : candidates) {
            if (result.size() >= requiredCount) {
                break;
            }

            int consecutiveDays = consecutiveDaysMap.getOrDefault(empId, 0);
            if (rule.getMaxConsecutiveDays() != null && consecutiveDays >= rule.getMaxConsecutiveDays()) {
                continue;
            }

            result.add(empId);
        }

        return result;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishPlan(Long planId) {
        SchedulePlan plan = getById(planId);
        if (plan == null) {
            throw new BusinessException("排班计划不存在");
        }
        if (plan.getPlanStatus() != 0) {
            throw new BusinessException("只有草稿状态的排班计划可以发布");
        }

        plan.setPlanStatus(1);
        plan.setPublishTime(LocalDateTime.now());
        return updateById(plan);
    }

    @Override
    public boolean cancelPlan(Long planId) {
        SchedulePlan plan = getById(planId);
        if (plan == null) {
            throw new BusinessException("排班计划不存在");
        }
        plan.setPlanStatus(2);
        return updateById(plan);
    }

    @Override
    public List<SchedulePlan> getActivePlans(String positionType, LocalDate date) {
        LambdaQueryWrapper<SchedulePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchedulePlan::getPositionType, positionType);
        wrapper.eq(SchedulePlan::getPlanStatus, 1);
        wrapper.le(SchedulePlan::getStartDate, date);
        wrapper.ge(SchedulePlan::getEndDate, date);
        return list(wrapper);
    }
}
