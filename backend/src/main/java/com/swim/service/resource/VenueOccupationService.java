package com.swim.service.resource;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.resource.VenueOccupation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface VenueOccupationService extends IService<VenueOccupation> {

    List<VenueOccupation> getOccupationsByDateRange(Long venueId, LocalDate startDate, LocalDate endDate);

    boolean checkConflict(Long venueId, LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeBusinessId);

    boolean addOccupation(VenueOccupation occupation);

    boolean cancelOccupation(String businessType, Long businessId);

    boolean updateOccupation(String businessType, Long businessId, LocalDate newDate,
                             LocalTime newStartTime, LocalTime newEndTime);
}
