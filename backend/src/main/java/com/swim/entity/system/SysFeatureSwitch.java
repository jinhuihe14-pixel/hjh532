package com.swim.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_feature_switch")
public class SysFeatureSwitch extends BaseEntity {

    private String featureCode;

    private String featureName;

    private String featureModule;

    private String description;

    private Integer isEnabled;

    private Integer sort;

    private String remark;
}
