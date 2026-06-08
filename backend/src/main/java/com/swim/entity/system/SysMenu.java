package com.swim.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private String menuName;

    private String menuCode;

    private String path;

    private String component;

    private String icon;

    private Long parentId;

    private Integer sort;

    private String menuType;

    private String permission;

    private Integer visible;

    private Integer status;
}
