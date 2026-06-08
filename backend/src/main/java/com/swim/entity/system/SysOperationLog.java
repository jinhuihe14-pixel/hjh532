package com.swim.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {

    private Long id;

    private String logNo;

    private String moduleType;

    private String businessType;

    private String operationType;

    private Long businessId;

    private String businessNo;

    private String operationContent;

    private String beforeData;

    private String afterData;

    private String changeFields;

    private Long operatorId;

    private String operatorName;

    private String operatorRole;

    private LocalDateTime operationTime;

    private String operationIp;

    private String operationLocation;

    private String userAgent;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private String responseResult;

    private Long costTime;

    private Integer operationStatus;

    private String errorMsg;

    private String remark;

    private LocalDateTime createTime;
}
