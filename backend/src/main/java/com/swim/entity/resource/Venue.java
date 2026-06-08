package com.swim.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("venue")
public class Venue extends BaseEntity {

    private String venueName;

    private String venueCode;

    private String venueType;

    private String location;

    private BigDecimal area;

    private Integer capacity;

    private Integer status;

    private String description;
}
