package com.swim.entity.training;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student")
public class Student extends BaseEntity {

    private String studentNo;

    private String studentName;

    private Integer gender;

    private LocalDate birthday;

    private Integer age;

    private Long memberId;

    private Long subAccountId;

    private String guardianName;

    private String guardianPhone;

    private String idCard;

    private String avatar;

    private String skillLevel;

    private String healthCondition;

    private Integer status;

    private String remark;
}
