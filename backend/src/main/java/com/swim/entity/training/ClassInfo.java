package com.swim.entity.training;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("class_info")
public class ClassInfo extends BaseEntity {

    private String className;

    private String classNo;

    private Long courseId;

    private String courseName;

    private String courseType;

    private Long coachId;

    private String coachName;

    private Long assistantCoachId;

    private Long venueId;

    private LocalDate classDateStart;

    private LocalDate classDateEnd;

    private BigDecimal totalHours;

    private BigDecimal completedHours;

    private BigDecimal remainingHours;

    private Integer studentCount;

    private Integer maxStudent;

    private Integer classStatus;

    private String weekDay;

    private LocalTime classStartTime;

    private LocalTime classEndTime;

    private BigDecimal price;

    private String remark;
}
