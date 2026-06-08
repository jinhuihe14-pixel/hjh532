package com.swim.service.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.system.SysOperationLog;

import java.time.LocalDateTime;

public interface SysOperationLogService extends IService<SysOperationLog> {

    Page<SysOperationLog> getLogPage(PageQuery query, String moduleType, String businessType,
                                      Long operatorId, String keyword, LocalDateTime startTime, LocalDateTime endTime);

    void addLog(String moduleType, String businessType, String operationType,
                Long businessId, String businessNo, String operationContent,
                String beforeData, String afterData, String changeFields,
                Long operatorId, String operatorName, String operatorRole,
                String operationIp, String requestMethod, String requestUrl,
                String requestParams, String responseResult, Long costTime,
                Integer operationStatus, String errorMsg);

    SysOperationLog getDetail(Long id);
}
