package com.swim.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.system.SysFeatureSwitch;

import java.util.List;

public interface SysFeatureSwitchService extends IService<SysFeatureSwitch> {

    List<SysFeatureSwitch> getAllSwitches();

    List<SysFeatureSwitch> getByModule(String featureModule);

    SysFeatureSwitch getByCode(String featureCode);

    boolean isEnabled(String featureCode);

    boolean enableFeature(String featureCode);

    boolean disableFeature(String featureCode);

    boolean updateSwitch(Long id, Integer isEnabled, String remark);
}
