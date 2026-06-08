package com.swim.controller.group;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swim.common.PageQuery;
import com.swim.common.Result;
import com.swim.entity.group.*;
import com.swim.service.group.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Tag(name = "团体课程&企业团单管理")
public class GroupController {

    private final GroupCustomerService groupCustomerService;
    private final GroupOrderService groupOrderService;
    private final GroupOrderVerificationService verificationService;
    private final GroupOrderExtensionService extensionService;
    private final GroupClassService groupClassService;
    private final GroupClassScheduleService classScheduleService;

    @GetMapping("/customer/page")
    public Result<Page<GroupCustomer>> getCustomerPage(PageQuery query,
                                                        @RequestParam(required = false) String customerType,
                                                        @RequestParam(required = false) String customerLevel,
                                                        @RequestParam(required = false) String keyword) {
        return Result.success(groupCustomerService.getCustomerPage(query, customerType, customerLevel, keyword));
    }

    @GetMapping("/customer/list")
    public Result<List<GroupCustomer>> getCustomerList() {
        return Result.success(groupCustomerService.getActiveList());
    }

    @GetMapping("/customer/{id}")
    public Result<GroupCustomer> getCustomer(@PathVariable Long id) {
        return Result.success(groupCustomerService.getById(id));
    }

    @PostMapping("/customer")
    public Result<Void> addCustomer(@RequestBody GroupCustomer customer) {
        groupCustomerService.addCustomer(customer);
        return Result.success();
    }

    @PutMapping("/customer")
    public Result<Void> updateCustomer(@RequestBody GroupCustomer customer) {
        groupCustomerService.updateCustomer(customer);
        return Result.success();
    }

    @DeleteMapping("/customer/{id}")
    public Result<Void> deleteCustomer(@PathVariable Long id) {
        groupCustomerService.removeById(id);
        return Result.success();
    }

    @GetMapping("/order/page")
    public Result<Page<GroupOrder>> getOrderPage(PageQuery query,
                                                  @RequestParam(required = false) Long customerId,
                                                  @RequestParam(required = false) Integer orderStatus,
                                                  @RequestParam(required = false) Integer payStatus,
                                                  @RequestParam(required = false) String orderType) {
        return Result.success(groupOrderService.getOrderPage(query, customerId, orderStatus, payStatus, orderType));
    }

    @GetMapping("/order/{id}")
    public Result<GroupOrder> getOrder(@PathVariable Long id) {
        return Result.success(groupOrderService.getById(id));
    }

    @PostMapping("/order")
    public Result<GroupOrder> createOrder(@RequestBody GroupOrder order) {
        return Result.success(groupOrderService.createOrder(order));
    }

    @PutMapping("/order")
    public Result<Void> updateOrder(@RequestBody GroupOrder order) {
        groupOrderService.updateById(order);
        return Result.success();
    }

    @PostMapping("/order/confirm/{id}")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        groupOrderService.confirmOrder(id);
        return Result.success();
    }

    @PostMapping("/order/cancel/{id}")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        groupOrderService.cancelOrder(id);
        return Result.success();
    }

    @PostMapping("/order/finish/{id}")
    public Result<Void> finishOrder(@PathVariable Long id) {
        groupOrderService.finishOrder(id);
        return Result.success();
    }

    @PostMapping("/order/lock-venue/{id}")
    public Result<Void> lockVenue(@PathVariable Long id) {
        groupOrderService.lockVenue(id);
        return Result.success();
    }

    @PostMapping("/order/unlock-venue/{id}")
    public Result<Void> unlockVenue(@PathVariable Long id) {
        groupOrderService.unlockVenue(id);
        return Result.success();
    }

    @GetMapping("/verification/page")
    public Result<Page<GroupOrderVerification>> getVerificationPage(PageQuery query,
                                                                     @RequestParam(required = false) Long orderId,
                                                                     @RequestParam(required = false) Long venueId,
                                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(verificationService.getVerificationPage(query, orderId, venueId, startDate, endDate));
    }

    @GetMapping("/verification/list/{orderId}")
    public Result<List<GroupOrderVerification>> getVerificationList(@PathVariable Long orderId) {
        return Result.success(verificationService.getByOrderId(orderId));
    }

    @PostMapping("/verification")
    public Result<GroupOrderVerification> verify(@RequestParam Long orderId,
                                                  @RequestParam Long venueId,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate verifyDate,
                                                  @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime startTime,
                                                  @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime endTime,
                                                  @RequestParam(defaultValue = "0") Integer actualPeople,
                                                  @RequestParam(defaultValue = "1") Integer usedCount) {
        return Result.success(verificationService.verify(orderId, venueId, verifyDate, startTime, endTime, actualPeople, usedCount));
    }

    @PostMapping("/verification/cancel/{id}")
    public Result<Void> cancelVerification(@PathVariable Long id, @RequestParam(required = false) String reason) {
        verificationService.cancelVerification(id, reason);
        return Result.success();
    }

    @GetMapping("/extension/list/{orderId}")
    public Result<List<GroupOrderExtension>> getExtensionList(@PathVariable Long orderId) {
        return Result.success(extensionService.getByOrderId(orderId));
    }

    @PostMapping("/extension")
    public Result<GroupOrderExtension> applyExtension(@RequestParam Long orderId,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate newEndDate,
                                                       @RequestParam(required = false) String reason) {
        return Result.success(extensionService.applyExtension(orderId, newEndDate, reason));
    }

    @PostMapping("/extension/approve/{id}")
    public Result<Void> approveExtension(@PathVariable Long id,
                                          @RequestParam Integer approvalStatus,
                                          @RequestParam(required = false) String opinion) {
        extensionService.approve(id, approvalStatus, opinion);
        return Result.success();
    }

    @GetMapping("/class/page")
    public Result<Page<GroupClass>> getClassPage(PageQuery query,
                                                  @RequestParam(required = false) Long customerId,
                                                  @RequestParam(required = false) Integer classStatus,
                                                  @RequestParam(required = false) String courseType) {
        return Result.success(groupClassService.getClassPage(query, customerId, classStatus, courseType));
    }

    @GetMapping("/class/{id}")
    public Result<GroupClass> getGroupClass(@PathVariable Long id) {
        return Result.success(groupClassService.getById(id));
    }

    @PostMapping("/class")
    public Result<GroupClass> createClass(@RequestBody GroupClass groupClass) {
        return Result.success(groupClassService.createClass(groupClass));
    }

    @PutMapping("/class")
    public Result<Void> updateClass(@RequestBody GroupClass groupClass) {
        groupClassService.updateById(groupClass);
        return Result.success();
    }

    @PostMapping("/class/cancel/{id}")
    public Result<Void> cancelClass(@PathVariable Long id) {
        groupClassService.cancelClass(id);
        return Result.success();
    }

    @PostMapping("/class/finish/{id}")
    public Result<Void> finishClass(@PathVariable Long id) {
        groupClassService.finishClass(id);
        return Result.success();
    }

    @GetMapping("/class-schedule/page")
    public Result<Page<GroupClassSchedule>> getClassSchedulePage(PageQuery query,
                                                                  @RequestParam(required = false) Long classId,
                                                                  @RequestParam(required = false) Long coachId,
                                                                  @RequestParam(required = false) Long venueId,
                                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(classScheduleService.getSchedulePage(query, classId, coachId, venueId, startDate, endDate));
    }

    @GetMapping("/class-schedule/list/{classId}")
    public Result<List<GroupClassSchedule>> getClassScheduleList(@PathVariable Long classId) {
        return Result.success(classScheduleService.getByClassId(classId));
    }

    @PostMapping("/class-schedule")
    public Result<GroupClassSchedule> createSchedule(@RequestBody GroupClassSchedule schedule) {
        return Result.success(classScheduleService.createSchedule(schedule));
    }

    @PostMapping("/class-schedule/generate/{classId}")
    public Result<List<GroupClassSchedule>> generateSchedules(@PathVariable Long classId) {
        return Result.success(classScheduleService.generateSchedules(classId));
    }

    @PostMapping("/class-schedule/cancel/{id}")
    public Result<Void> cancelSchedule(@PathVariable Long id) {
        classScheduleService.cancelSchedule(id);
        return Result.success();
    }

    @PostMapping("/class-schedule/complete/{id}")
    public Result<Void> completeSchedule(@PathVariable Long id,
                                          @RequestParam(defaultValue = "0") Integer actualStudentCount,
                                          @RequestParam(required = false) String teachingContent) {
        classScheduleService.completeSchedule(id, actualStudentCount, teachingContent);
        return Result.success();
    }
}
