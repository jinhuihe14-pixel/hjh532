package com.swim.mapper.resource;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.resource.Venue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VenueMapper extends BaseMapper<Venue> {
}
