package com.swim.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.entity.system.SysFeatureSwitch;
import com.swim.mapper.system.SysFeatureSwitchMapper;
import com.swim.service.system.SysFeatureSwitchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysFeatureSwitchServiceImpl extends ServiceImpl<SysFeatureSwitchMapper, SysFeatureSwitch> implements SysFeatureSwitchService {

    @Override
    public List<SysFeatureSwitch> getAllSwitches() {
        LambdaQueryWrapper<SysFeatureSwitch> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysFeatureSwitch::getSort);
        return list(wrapper);
    }

    @Override
    public List<SysFeatureSwitch> getByModule(String featureModule) {
        LambdaQueryWrapper<SysFeatureSwitch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysFeatureSwitch::getFeatureModule, featureModule);
        wrapper.orderByAsc(SysFeatureSwitch::getSort);
        return list(wrapper);
    }

    @Override
    public SysFeatureSwitch getByCode(String featureCode) {
        LambdaQueryWrapper<SysFeatureSwitch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysFeatureSwitch::getFeatureCode, featureCode);
        return getOne(wrapper);
    }

    @Override
    public boolean isEnabled(String featureCode) {
        SysFeatureSwitch feature = getByCode(featureCode);
        return feature != null && feature.getIsEnabled() != null && feature.getIsEnabled() == 1;
    }

    @Override
    public boolean enableFeature(String featureCode) {
        SysFeatureSwitch feature = getByCode(featureCode);
        if (feature == null) {
            throw new BusinessException("功能开关不存在");
        }
        feature.setIsEnabled(1);
        return updateById(feature);
    }

    @Override
    public boolean disableFeature(String featureCode) {
        SysFeatureSwitch feature = getByCode(featureCode);
        if (feature == null) {
            throw new BusinessException("功能开关不存在");
        }
        feature.setIsEnabled(0);
        return updateById(feature);
    }

    @Override
    public boolean updateSwitch(Long id, Integer isEnabled, String remark) {
        SysFeatureSwitch feature = getById(id);
        if (feature == null) {
            throw new BusinessException("功能开关不存在");
        }
        if (isEnabled != null) {
            feature.setIsEnabled(isEnabled);
        }
        if (remark != null) {
            feature.setRemark(remark);
        }
        return updateById(feature);
    }
}
