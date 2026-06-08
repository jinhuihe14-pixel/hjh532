package com.swim.entity.training;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hours_log")
public class HoursLog {

    private Long id;

    private String logNo;

    private Long studentId;

    private Long classId;

    private Long scheduleId;

    private Long cardId;

    private String hoursType;

    private String changeType;

    private BigDecimal changeHours;

    private BigDecimal beforeHours;

    private BigDecimal afterHours;

    private String businessType;

    private Long businessId;

    private String businessNo;

    private Long operatorId;

    private String operatorName;

    private String remark;

    private LocalDateTime createTime;
}
