package com.swim.entity.training;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {

    private String courseName;

    private String courseType;

    private String courseCode;

    private String ageRange;

    private String skillLevel;

    private BigDecimal totalHours;

    private Integer studentCount;

    private Integer maxStudent;

    private Long venueId;

    private BigDecimal price;

    private String courseIntroduction;

    private String syllabus;

    private Integer status;

    private Integer sort;
}
