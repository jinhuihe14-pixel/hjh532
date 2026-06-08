package com.swim.entity.training;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("student_attendance")
public class StudentAttendance {

    private Long id;

    private Long scheduleId;

    private Long classId;

    private Long studentId;

    private String studentName;

    private Integer attendanceStatus;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private BigDecimal hoursConsumed;

    private Integer isMakeup;

    private Long makeupScheduleId;

    private Integer needMakeup;

    private Integer makeupStatus;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
