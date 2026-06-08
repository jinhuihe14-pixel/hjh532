package com.swim.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    private Integer status;

    private String position;

    private Long departmentId;

    private String employeeNo;
}
