package com.swim.entity.training;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("class_student")
public class ClassStudent {

    private Long id;

    private Long classId;

    private Long studentId;

    private String studentName;

    private LocalDateTime enrollTime;

    private String enrollmentType;

    private BigDecimal enrolledHours;

    private BigDecimal consumedHours;

    private BigDecimal remainingHours;

    private Integer studentStatus;

    private LocalDate suspensionStart;

    private LocalDate suspensionEnd;

    private Long sourceClassId;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
