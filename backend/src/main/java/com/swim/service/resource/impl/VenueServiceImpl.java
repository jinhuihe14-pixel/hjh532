package com.swim.service.resource.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.resource.Venue;
import com.swim.mapper.resource.VenueMapper;
import com.swim.service.resource.VenueService;
import org.springframework.stereotype.Service;

@Service
public class VenueServiceImpl extends ServiceImpl<VenueMapper, Venue> implements VenueService {
}
