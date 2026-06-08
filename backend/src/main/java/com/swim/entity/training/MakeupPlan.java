package com.swim.entity.training;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("makeup_plan")
public class MakeupPlan {

    private Long id;

    private String planNo;

    private Long studentId;

    private String studentName;

    private Long originalScheduleId;

    private Long originalClassId;

    private Long targetClassId;

    private Long targetScheduleId;

    private Integer makeupStatus;

    private LocalDate deadline;

    private LocalDateTime arrangementTime;

    private LocalDateTime completeTime;

    private LocalDateTime cancelTime;

    private String cancelReason;

    private Long operatorId;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
