package com.swim.controller.resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swim.common.PageQuery;
import com.swim.common.Result;
import com.swim.entity.resource.Venue;
import com.swim.entity.resource.VenueOccupation;
import com.swim.entity.resource.TimeSlotTemplate;
import com.swim.service.resource.VenueOccupationService;
import com.swim.service.resource.VenueService;
import com.swim.service.resource.TimeSlotTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
@Tag(name = "资源管理")
public class ResourceController {

    private final VenueService venueService;
    private final VenueOccupationService venueOccupationService;
    private final TimeSlotTemplateService timeSlotTemplateService;

    @GetMapping("/venue/list")
    public Result<Page<Venue>> getVenueList(PageQuery query, String venueType, String keyword) {
        Page<Venue> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Venue> wrapper = new LambdaQueryWrapper<>();
        if (venueType != null && !venueType.isEmpty()) {
            wrapper.eq(Venue::getVenueType, venueType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Venue::getVenueName, keyword);
        }
        wrapper.orderByAsc(Venue::getId);
        return Result.success(venueService.page(page, wrapper));
    }

    @GetMapping("/venue/{id}")
    public Result<Venue> getVenue(@PathVariable Long id) {
        return Result.success(venueService.getById(id));
    }

    @PostMapping("/venue")
    public Result<Void> addVenue(@RequestBody Venue venue) {
        venueService.save(venue);
        return Result.success();
    }

    @PutMapping("/venue")
    public Result<Void> updateVenue(@RequestBody Venue venue) {
        venueService.updateById(venue);
        return Result.success();
    }

    @DeleteMapping("/venue/{id}")
    public Result<Void> deleteVenue(@PathVariable Long id) {
        venueService.removeById(id);
        return Result.success();
    }

    @GetMapping("/occupation")
    public Result<List<VenueOccupation>> getOccupations(
            @RequestParam Long venueId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return Result.success(venueOccupationService.getOccupationsByDateRange(venueId, startDate, endDate));
    }

    @GetMapping("/occupation/check")
    public Result<Boolean> checkConflict(
            @RequestParam Long venueId,
            @RequestParam LocalDate date,
            @RequestParam java.time.LocalTime startTime,
            @RequestParam java.time.LocalTime endTime) {
        boolean conflict = venueOccupationService.checkConflict(venueId, date, startTime, endTime, null);
        return Result.success(conflict);
    }

    @GetMapping("/time-slot/list")
    public Result<List<TimeSlotTemplate>> getTimeSlotList(@RequestParam Long venueId, String usageType) {
        LambdaQueryWrapper<TimeSlotTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TimeSlotTemplate::getVenueId, venueId);
        if (usageType != null && !usageType.isEmpty()) {
            wrapper.eq(TimeSlotTemplate::getUsageType, usageType);
        }
        wrapper.eq(TimeSlotTemplate::getStatus, 1);
        wrapper.orderByAsc(TimeSlotTemplate::getSort);
        return Result.success(timeSlotTemplateService.list(wrapper));
    }
}
