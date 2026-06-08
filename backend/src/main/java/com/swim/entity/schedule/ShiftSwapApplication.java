package com.swim.entity.schedule;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shift_swap_application")
public class ShiftSwapApplication extends BaseEntity {

    private String swapNo;

    private Long applicantId;

    private String applicantName;

    private Long applicantScheduleId;

    private LocalDate applicantDate;

    private Long applicantShiftId;

    private Long targetEmployeeId;

    private String targetEmployeeName;

    private Long targetScheduleId;

    private LocalDate targetDate;

    private Long targetShiftId;

    private String swapType;

    private String applyReason;

    private Integer targetConfirmStatus;

    private LocalDateTime targetConfirmTime;

    private String targetConfirmOpinion;

    private Integer approvalStatus;

    private Long approverId;

    private String approverName;

    private LocalDateTime approvalTime;

    private String approvalOpinion;

    private Integer swapStatus;

    private LocalDateTime effectiveTime;

    private LocalDateTime cancelTime;

    private String remark;
}
