package com.swim.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("venue_occupation")
public class VenueOccupation extends BaseEntity {

    private Long venueId;

    private LocalDate occupationDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String usageType;

    private String businessType;

    private Long businessId;

    private String businessNo;

    private Integer lockStatus;

    private Integer status;
}
