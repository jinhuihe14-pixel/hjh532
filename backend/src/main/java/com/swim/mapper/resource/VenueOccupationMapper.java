package com.swim.mapper.resource;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.resource.VenueOccupation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface VenueOccupationMapper extends BaseMapper<VenueOccupation> {

    List<VenueOccupation> selectOccupationsByDateRange(
            @Param("venueId") Long venueId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Integer status
    );

    List<VenueOccupation> checkConflict(
            @Param("venueId") Long venueId,
            @Param("occupationDate") LocalDate occupationDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeBusinessId") Long excludeBusinessId,
            @Param("status") Integer status
    );
}
