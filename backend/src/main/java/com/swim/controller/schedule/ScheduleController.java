package com.swim.controller.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swim.common.PageQuery;
import com.swim.common.Result;
import com.swim.entity.schedule.*;
import com.swim.service.schedule.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Tag(name = "智能排班管理")
public class ScheduleController {

    private final ShiftTemplateService shiftTemplateService;
    private final ScheduleRuleService scheduleRuleService;
    private final EmployeeSkillService employeeSkillService;
    private final EmployeeSchedulePreferenceService employeePreferenceService;
    private final SchedulePlanService schedulePlanService;
    private final ScheduleDetailService scheduleDetailService;
    private final ShiftSwapApplicationService shiftSwapApplicationService;
    private final HolidayConfigService holidayConfigService;

    @GetMapping("/shift/list")
    public Result<List<ShiftTemplate>> getShiftList() {
        return Result.success(shiftTemplateService.getActiveList());
    }

    @GetMapping("/shift/{id}")
    public Result<ShiftTemplate> getShift(@PathVariable Long id) {
        return Result.success(shiftTemplateService.getById(id));
    }

    @PostMapping("/shift")
    public Result<Void> addShift(@RequestBody ShiftTemplate shift) {
        shiftTemplateService.save(shift);
        return Result.success();
    }

    @PutMapping("/shift")
    public Result<Void> updateShift(@RequestBody ShiftTemplate shift) {
        shiftTemplateService.updateById(shift);
        return Result.success();
    }

    @DeleteMapping("/shift/{id}")
    public Result<Void> deleteShift(@PathVariable Long id) {
        shiftTemplateService.removeById(id);
        return Result.success();
    }

    @GetMapping("/rule/list")
    public Result<List<ScheduleRule>> getRuleList() {
        LambdaQueryWrapper<ScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ScheduleRule::getId);
        return Result.success(scheduleRuleService.list(wrapper));
    }

    @GetMapping("/rule/{id}")
    public Result<ScheduleRule> getRule(@PathVariable Long id) {
        return Result.success(scheduleRuleService.getById(id));
    }

    @PostMapping("/rule")
    public Result<Void> addRule(@RequestBody ScheduleRule rule) {
        scheduleRuleService.save(rule);
        return Result.success();
    }

    @PutMapping("/rule")
    public Result<Void> updateRule(@RequestBody ScheduleRule rule) {
        scheduleRuleService.updateById(rule);
        return Result.success();
    }

    @DeleteMapping("/rule/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        scheduleRuleService.removeById(id);
        return Result.success();
    }

    @GetMapping("/employee-skill/{employeeId}")
    public Result<List<EmployeeSkill>> getEmployeeSkills(@PathVariable Long employeeId) {
        return Result.success(employeeSkillService.getByEmployeeId(employeeId));
    }

    @PostMapping("/employee-skill")
    public Result<Void> addEmployeeSkill(@RequestBody EmployeeSkill skill) {
        employeeSkillService.save(skill);
        return Result.success();
    }

    @PutMapping("/employee-skill")
    public Result<Void> updateEmployeeSkill(@RequestBody EmployeeSkill skill) {
        employeeSkillService.updateById(skill);
        return Result.success();
    }

    @DeleteMapping("/employee-skill/{id}")
    public Result<Void> deleteEmployeeSkill(@PathVariable Long id) {
        employeeSkillService.removeById(id);
        return Result.success();
    }

    @GetMapping("/preference/{employeeId}")
    public Result<EmployeeSchedulePreference> getEmployeePreference(@PathVariable Long employeeId) {
        return Result.success(employeePreferenceService.getByEmployeeId(employeeId));
    }

    @PostMapping("/preference")
    public Result<Void> savePreference(@RequestBody EmployeeSchedulePreference preference) {
        EmployeeSchedulePreference exist = employeePreferenceService.getByEmployeeId(preference.getEmployeeId());
        if (exist != null) {
            preference.setId(exist.getId());
            employeePreferenceService.updateById(preference);
        } else {
            employeePreferenceService.save(preference);
        }
        return Result.success();
    }

    @GetMapping("/plan/page")
    public Result<Page<SchedulePlan>> getPlanPage(PageQuery query,
                                                   @RequestParam(required = false) String positionType,
                                                   @RequestParam(required = false) Integer planStatus) {
        return Result.success(schedulePlanService.getPlanPage(query, positionType, planStatus));
    }

    @GetMapping("/plan/{id}")
    public Result<SchedulePlan> getPlan(@PathVariable Long id) {
        return Result.success(schedulePlanService.getById(id));
    }

    @PostMapping("/plan/generate")
    public Result<SchedulePlan> generateSchedule(@RequestParam String positionType,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                  @RequestParam(defaultValue = "WEEKLY") String scheduleType) {
        return Result.success(schedulePlanService.generateSchedule(positionType, startDate, endDate, scheduleType));
    }

    @PostMapping("/plan/publish/{id}")
    public Result<Void> publishPlan(@PathVariable Long id) {
        schedulePlanService.publishPlan(id);
        return Result.success();
    }

    @PostMapping("/plan/cancel/{id}")
    public Result<Void> cancelPlan(@PathVariable Long id) {
        schedulePlanService.cancelPlan(id);
        return Result.success();
    }

    @GetMapping("/detail/list/{planId}")
    public Result<List<ScheduleDetail>> getDetailList(@PathVariable Long planId) {
        return Result.success(scheduleDetailService.getByPlanId(planId));
    }

    @PutMapping("/detail")
    public Result<Void> updateDetail(@RequestBody ScheduleDetail detail) {
        scheduleDetailService.updateDetail(detail);
        return Result.success();
    }

    @GetMapping("/detail/employee")
    public Result<List<ScheduleDetail>> getEmployeeSchedule(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(scheduleDetailService.getByEmployeeAndDateRange(employeeId, startDate, endDate));
    }

    @GetMapping("/detail/position")
    public Result<List<ScheduleDetail>> getPositionSchedule(
            @RequestParam String positionType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(scheduleDetailService.getByPositionAndDate(positionType, date));
    }

    @GetMapping("/swap/page")
    public Result<Page<ShiftSwapApplication>> getSwapPage(PageQuery query,
                                                           @RequestParam(required = false) Long employeeId,
                                                           @RequestParam(required = false) Integer swapStatus) {
        return Result.success(shiftSwapApplicationService.getApplicationPage(query, employeeId, swapStatus));
    }

    @GetMapping("/swap/{id}")
    public Result<ShiftSwapApplication> getSwapDetail(@PathVariable Long id) {
        return Result.success(shiftSwapApplicationService.getDetail(id));
    }

    @PostMapping("/swap")
    public Result<Void> applySwap(@RequestBody ShiftSwapApplication application) {
        shiftSwapApplicationService.applySwap(application);
        return Result.success();
    }

    @PostMapping("/swap/confirm/{id}")
    public Result<Void> confirmSwap(@PathVariable Long id,
                                     @RequestParam Integer confirmStatus,
                                     @RequestParam(required = false) String opinion) {
        shiftSwapApplicationService.confirmByTarget(id, confirmStatus, opinion);
        return Result.success();
    }

    @PostMapping("/swap/approve/{id}")
    public Result<Void> approveSwap(@PathVariable Long id,
                                     @RequestParam Integer approvalStatus,
                                     @RequestParam(required = false) String opinion) {
        shiftSwapApplicationService.approve(id, approvalStatus, opinion);
        return Result.success();
    }

    @PostMapping("/swap/cancel/{id}")
    public Result<Void> cancelSwap(@PathVariable Long id) {
        shiftSwapApplicationService.cancelApplication(id);
        return Result.success();
    }

    @GetMapping("/holiday/list")
    public Result<List<HolidayConfig>> getHolidayList(@RequestParam Integer year) {
        return Result.success(holidayConfigService.getByYear(year));
    }

    @PostMapping("/holiday")
    public Result<Void> addHoliday(@RequestBody HolidayConfig holiday) {
        holidayConfigService.save(holiday);
        return Result.success();
    }

    @PutMapping("/holiday")
    public Result<Void> updateHoliday(@RequestBody HolidayConfig holiday) {
        holidayConfigService.updateById(holiday);
        return Result.success();
    }

    @DeleteMapping("/holiday/{id}")
    public Result<Void> deleteHoliday(@PathVariable Long id) {
        holidayConfigService.removeById(id);
        return Result.success();
    }
}
