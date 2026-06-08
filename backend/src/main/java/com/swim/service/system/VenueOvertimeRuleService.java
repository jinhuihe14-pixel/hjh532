package com.swim.service.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.system.VenueOvertimeRule;

import java.util.List;

public interface VenueOvertimeRuleService extends IService<VenueOvertimeRule> {

    Page<VenueOvertimeRule> getRulePage(PageQuery query, String venueType, Long venueId, Integer status);

    VenueOvertimeRule createRule(VenueOvertimeRule rule);

    boolean updateRule(VenueOvertimeRule rule);

    VenueOvertimeRule getMatchRule(Long venueId, String venueType, String rentalType);

    List<VenueOvertimeRule> getActiveRules();
}
