package com.swim.controller.training;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swim.common.PageQuery;
import com.swim.common.Result;
import com.swim.entity.training.*;
import com.swim.service.training.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/training")
@RequiredArgsConstructor
@Tag(name = "培训管理")
public class TrainingController {

    private final CourseService courseService;
    private final ClassInfoService classInfoService;
    private final StudentService studentService;
    private final ClassScheduleService classScheduleService;
    private final AttendanceService attendanceService;
    private final MakeupPlanService makeupPlanService;

    @GetMapping("/course/page")
    public Result<Page<Course>> getCoursePage(PageQuery query, String courseType, String skillLevel, Integer status) {
        Page<Course> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (courseType != null && !courseType.isEmpty()) {
            wrapper.eq(Course::getCourseType, courseType);
        }
        if (skillLevel != null && !skillLevel.isEmpty()) {
            wrapper.eq(Course::getSkillLevel, skillLevel);
        }
        if (status != null) {
            wrapper.eq(Course::getStatus, status);
        }
        wrapper.orderByAsc(Course::getSort);
        return Result.success(courseService.page(page, wrapper));
    }

    @PostMapping("/course")
    public Result<Void> addCourse(@RequestBody Course course) {
        courseService.save(course);
        return Result.success();
    }

    @PutMapping("/course")
    public Result<Void> updateCourse(@RequestBody Course course) {
        courseService.updateById(course);
        return Result.success();
    }

    @GetMapping("/class/page")
    public Result<Page<ClassInfo>> getClassPage(PageQuery query, Long courseId, Long coachId,
                                                 Integer classStatus, String className) {
        Page<ClassInfo> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ClassInfo> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) {
            wrapper.eq(ClassInfo::getCourseId, courseId);
        }
        if (coachId != null) {
            wrapper.eq(ClassInfo::getCoachId, coachId);
        }
        if (classStatus != null) {
            wrapper.eq(ClassInfo::getClassStatus, classStatus);
        }
        if (className != null && !className.isEmpty()) {
            wrapper.like(ClassInfo::getClassName, className);
        }
        wrapper.orderByDesc(ClassInfo::getCreateTime);
        return Result.success(classInfoService.page(page, wrapper));
    }

    @GetMapping("/class/{id}")
    public Result<ClassInfo> getClass(@PathVariable Long id) {
        return Result.success(classInfoService.getById(id));
    }

    @PostMapping("/class")
    public Result<ClassInfo> createClass(@RequestBody ClassInfo classInfo) {
        return Result.success(classInfoService.createClass(classInfo));
    }

    @PostMapping("/class/{classId}/enroll")
    public Result<Void> enrollStudent(@PathVariable Long classId,
                                      @RequestParam Long studentId,
                                      @RequestParam BigDecimal hours) {
        classInfoService.enrollStudent(classId, studentId, hours);
        return Result.success();
    }

    @PostMapping("/class/transfer")
    public Result<Void> transferStudent(@RequestParam Long fromClassId,
                                        @RequestParam Long toClassId,
                                        @RequestParam Long studentId,
                                        @RequestParam BigDecimal hours) {
        classInfoService.transferStudent(fromClassId, toClassId, studentId, hours);
        return Result.success();
    }

    @PostMapping("/class/{classId}/suspend")
    public Result<Void> suspendStudent(@PathVariable Long classId,
                                       @RequestParam Long studentId,
                                       @RequestParam LocalDate startDate,
                                       @RequestParam LocalDate endDate) {
        classInfoService.suspendStudent(classId, studentId, startDate, endDate);
        return Result.success();
    }

    @PostMapping("/class/{classId}/resume")
    public Result<Void> resumeStudent(@PathVariable Long classId,
                                      @RequestParam Long studentId) {
        classInfoService.resumeStudent(classId, studentId);
        return Result.success();
    }

    @GetMapping("/student/page")
    public Result<Page<Student>> getStudentPage(PageQuery query, String keyword, String skillLevel, Integer status) {
        Page<Student> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Student::getStudentName, keyword)
                    .or().like(Student::getStudentNo, keyword)
                    .or().like(Student::getGuardianPhone, keyword));
        }
        if (skillLevel != null && !skillLevel.isEmpty()) {
            wrapper.eq(Student::getSkillLevel, skillLevel);
        }
        if (status != null) {
            wrapper.eq(Student::getStatus, status);
        }
        wrapper.orderByDesc(Student::getCreateTime);
        return Result.success(studentService.page(page, wrapper));
    }

    @PostMapping("/student")
    public Result<Void> addStudent(@RequestBody Student student) {
        studentService.save(student);
        return Result.success();
    }

    @PutMapping("/student")
    public Result<Void> updateStudent(@RequestBody Student student) {
        studentService.updateById(student);
        return Result.success();
    }

    @GetMapping("/schedule/list")
    public Result<List<ClassSchedule>> getScheduleList(Long classId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(ClassSchedule::getClassId, classId);
        }
        if (startDate != null) {
            wrapper.ge(ClassSchedule::getClassDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ClassSchedule::getClassDate, endDate);
        }
        wrapper.orderByAsc(ClassSchedule::getClassDate).orderByAsc(ClassSchedule::getStartTime);
        return Result.success(classScheduleService.list(wrapper));
    }

    @PostMapping("/schedule/generate/{classId}")
    public Result<List<ClassSchedule>> generateSchedules(@PathVariable Long classId) {
        return Result.success(classScheduleService.generateSchedules(classId));
    }

    @PostMapping("/schedule/{id}/cancel")
    public Result<Void> cancelSchedule(@PathVariable Long id) {
        classScheduleService.cancelSchedule(id);
        return Result.success();
    }

    @PostMapping("/schedule/{id}/reschedule")
    public Result<Void> reschedule(@PathVariable Long id,
                                   @RequestParam LocalDate newDate,
                                   @RequestParam LocalTime newStartTime,
                                   @RequestParam LocalTime newEndTime) {
        classScheduleService.reschedule(id, newDate, newStartTime, newEndTime);
        return Result.success();
    }

    @GetMapping("/attendance/{scheduleId}")
    public Result<List<StudentAttendance>> getAttendance(@PathVariable Long scheduleId) {
        return Result.success(attendanceService.getAttendanceBySchedule(scheduleId));
    }

    @PostMapping("/attendance")
    public Result<Void> recordAttendance(@RequestParam Long scheduleId,
                                         @RequestParam Long studentId,
                                         @RequestParam Integer status,
                                         @RequestParam(required = false) String remark) {
        attendanceService.recordAttendance(scheduleId, studentId, status, remark);
        return Result.success();
    }

    @PostMapping("/attendance/batch")
    public Result<Void> batchRecordAttendance(@RequestParam Long scheduleId,
                                              @RequestBody List<StudentAttendance> attendanceList) {
        attendanceService.batchRecordAttendance(scheduleId, attendanceList);
        return Result.success();
    }

    @GetMapping("/makeup/page")
    public Result<Page<MakeupPlan>> getMakeupPlanPage(PageQuery query, Long studentId, Integer makeupStatus) {
        Page<MakeupPlan> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<MakeupPlan> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(MakeupPlan::getStudentId, studentId);
        }
        if (makeupStatus != null) {
            wrapper.eq(MakeupPlan::getMakeupStatus, makeupStatus);
        }
        wrapper.orderByDesc(MakeupPlan::getCreateTime);
        return Result.success(makeupPlanService.page(page, wrapper));
    }
}
