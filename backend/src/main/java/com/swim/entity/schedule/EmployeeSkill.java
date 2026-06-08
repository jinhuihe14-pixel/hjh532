package com.swim.entity.schedule;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employee_skill")
public class EmployeeSkill extends BaseEntity {

    private Long employeeId;

    private String skillCode;

    private String skillName;

    private Integer skillLevel;

    private String certNo;

    private LocalDate certExpireDate;
}
