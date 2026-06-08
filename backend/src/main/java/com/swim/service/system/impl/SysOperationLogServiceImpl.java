package com.swim.service.system.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.PageQuery;
import com.swim.entity.system.SysOperationLog;
import com.swim.mapper.system.SysOperationLogMapper;
import com.swim.service.system.SysOperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    @Override
    public Page<SysOperationLog> getLogPage(PageQuery query, String moduleType, String businessType,
                                             Long operatorId, String keyword, LocalDateTime startTime, LocalDateTime endTime) {
        Page<SysOperationLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (moduleType != null && !moduleType.isEmpty()) {
            wrapper.eq(SysOperationLog::getModuleType, moduleType);
        }
        if (businessType != null && !businessType.isEmpty()) {
            wrapper.eq(SysOperationLog::getBusinessType, businessType);
        }
        if (operatorId != null) {
            wrapper.eq(SysOperationLog::getOperatorId, operatorId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysOperationLog::getOperatorName, keyword)
                    .or().like(SysOperationLog::getOperationType, keyword)
                    .or().like(SysOperationLog::getBusinessNo, keyword);
        }
        if (startTime != null && endTime != null) {
            wrapper.between(SysOperationLog::getOperationTime, startTime, endTime);
        }
        wrapper.orderByDesc(SysOperationLog::getOperationTime);
        return page(page, wrapper);
    }

    @Override
    @Async
    public void addLog(String moduleType, String businessType, String operationType,
                       Long businessId, String businessNo, String operationContent,
                       String beforeData, String afterData, String changeFields,
                       Long operatorId, String operatorName, String operatorRole,
                       String operationIp, String requestMethod, String requestUrl,
                       String requestParams, String responseResult, Long costTime,
                       Integer operationStatus, String errorMsg) {
        SysOperationLog log = new SysOperationLog();
        log.setLogNo("OL" + IdUtil.getSnowflakeNextIdStr());
        log.setModuleType(moduleType);
        log.setBusinessType(businessType);
        log.setOperationType(operationType);
        log.setBusinessId(businessId);
        log.setBusinessNo(businessNo);
        log.setOperationContent(operationContent);
        log.setBeforeData(beforeData);
        log.setAfterData(afterData);
        log.setChangeFields(changeFields);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperatorRole(operatorRole);
        log.setOperationTime(LocalDateTime.now());
        log.setOperationIp(operationIp);
        log.setRequestMethod(requestMethod);
        log.setRequestUrl(requestUrl);
        log.setRequestParams(requestParams);
        log.setResponseResult(responseResult);
        log.setCostTime(costTime);
        log.setOperationStatus(operationStatus != null ? operationStatus : 1);
        log.setErrorMsg(errorMsg);
        log.setCreateTime(LocalDateTime.now());
        save(log);
    }

    @Override
    public SysOperationLog getDetail(Long id) {
        return getById(id);
    }
}
