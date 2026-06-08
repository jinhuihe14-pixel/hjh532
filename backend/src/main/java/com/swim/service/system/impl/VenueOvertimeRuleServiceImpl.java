package com.swim.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.system.VenueOvertimeRule;
import com.swim.mapper.system.VenueOvertimeRuleMapper;
import com.swim.service.system.VenueOvertimeRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueOvertimeRuleServiceImpl extends ServiceImpl<VenueOvertimeRuleMapper, VenueOvertimeRule> implements VenueOvertimeRuleService {

    @Override
    public Page<VenueOvertimeRule> getRulePage(PageQuery query, String venueType, Long venueId, Integer status) {
        Page<VenueOvertimeRule> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<VenueOvertimeRule> wrapper = new LambdaQueryWrapper<>();
        if (venueType != null && !venueType.isEmpty()) {
            wrapper.and(w -> w.eq(VenueOvertimeRule::getVenueType, venueType).or().isNull(VenueOvertimeRule::getVenueType));
        }
        if (venueId != null) {
            wrapper.and(w -> w.eq(VenueOvertimeRule::getVenueId, venueId).or().isNull(VenueOvertimeRule::getVenueId));
        }
        if (status != null) {
            wrapper.eq(VenueOvertimeRule::getStatus, status);
        }
        wrapper.orderByAsc(VenueOvertimeRule::getVenueId);
        wrapper.orderByAsc(VenueOvertimeRule::getVenueType);
        return page(page, wrapper);
    }

    @Override
    public VenueOvertimeRule createRule(VenueOvertimeRule rule) {
        if (rule.getGraceMinutes() == null) {
            rule.setGraceMinutes(15);
        }
        if (rule.getBillingUnit() == null) {
            rule.setBillingUnit(30);
        }
        if (rule.getIsAutomatic() == null) {
            rule.setIsAutomatic(1);
        }
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        if (rule.getRateType() == null) {
            rule.setRateType("FIXED");
        }
        save(rule);
        return rule;
    }

    @Override
    public boolean updateRule(VenueOvertimeRule rule) {
        VenueOvertimeRule exist = getById(rule.getId());
        if (exist == null) {
            throw new BusinessException("规则不存在");
        }
        return updateById(rule);
    }

    @Override
    public VenueOvertimeRule getMatchRule(Long venueId, String venueType, String rentalType) {
        LambdaQueryWrapper<VenueOvertimeRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VenueOvertimeRule::getStatus, 1);

        if (venueId != null) {
            wrapper.eq(VenueOvertimeRule::getVenueId, venueId);
            if (rentalType != null && !rentalType.isEmpty()) {
                wrapper.eq(VenueOvertimeRule::getRentalType, rentalType);
            }
            List<VenueOvertimeRule> rules = list(wrapper);
            if (!rules.isEmpty()) {
                return rules.get(0);
            }
        }

        LambdaQueryWrapper<VenueOvertimeRule> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(VenueOvertimeRule::getStatus, 1);
        if (venueType != null && !venueType.isEmpty()) {
            typeWrapper.eq(VenueOvertimeRule::getVenueType, venueType);
        }
        if (rentalType != null && !rentalType.isEmpty()) {
            typeWrapper.eq(VenueOvertimeRule::getRentalType, rentalType);
        }
        List<VenueOvertimeRule> typeRules = list(typeWrapper);
        if (!typeRules.isEmpty()) {
            return typeRules.get(0);
        }

        LambdaQueryWrapper<VenueOvertimeRule> defaultWrapper = new LambdaQueryWrapper<>();
        defaultWrapper.eq(VenueOvertimeRule::getStatus, 1);
        defaultWrapper.isNull(VenueOvertimeRule::getVenueId);
        defaultWrapper.isNull(VenueOvertimeRule::getVenueType);
        defaultWrapper.isNull(VenueOvertimeRule::getRentalType);
        List<VenueOvertimeRule> defaultRules = list(defaultWrapper);
        return defaultRules.isEmpty() ? null : defaultRules.get(0);
    }

    @Override
    public List<VenueOvertimeRule> getActiveRules() {
        LambdaQueryWrapper<VenueOvertimeRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VenueOvertimeRule::getStatus, 1);
        wrapper.orderByAsc(VenueOvertimeRule::getId);
        return list(wrapper);
    }
}
