package com.swim.service.schedule.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.schedule.ScheduleDetail;
import com.swim.entity.schedule.ShiftSwapApplication;
import com.swim.mapper.schedule.ShiftSwapApplicationMapper;
import com.swim.service.schedule.ScheduleDetailService;
import com.swim.service.schedule.ShiftSwapApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShiftSwapApplicationServiceImpl extends ServiceImpl<ShiftSwapApplicationMapper, ShiftSwapApplication> implements ShiftSwapApplicationService {

    private final ScheduleDetailService scheduleDetailService;

    @Override
    public Page<ShiftSwapApplication> getApplicationPage(PageQuery query, Long employeeId, Integer swapStatus) {
        Page<ShiftSwapApplication> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ShiftSwapApplication> wrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            wrapper.and(w -> w.eq(ShiftSwapApplication::getApplicantId, employeeId)
                    .or().eq(ShiftSwapApplication::getTargetEmployeeId, employeeId));
        }
        if (swapStatus != null) {
            wrapper.eq(ShiftSwapApplication::getSwapStatus, swapStatus);
        }
        wrapper.orderByDesc(ShiftSwapApplication::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applySwap(ShiftSwapApplication application) {
        ScheduleDetail applicantDetail = scheduleDetailService.getById(application.getApplicantScheduleId());
        if (applicantDetail == null) {
            throw new BusinessException("申请人排班不存在");
        }

        application.setSwapNo("SSA" + IdUtil.getSnowflakeNextIdStr());
        application.setApplicantDate(applicantDetail.getScheduleDate());
        application.setApplicantShiftId(applicantDetail.getShiftId());
        application.setTargetConfirmStatus(0);
        application.setApprovalStatus(0);
        application.setSwapStatus(0);
        return save(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmByTarget(Long id, Integer confirmStatus, String opinion) {
        ShiftSwapApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("调班申请不存在");
        }
        if (application.getSwapStatus() != 0) {
            throw new BusinessException("当前状态不可确认");
        }

        application.setTargetConfirmStatus(confirmStatus);
        application.setTargetConfirmTime(LocalDateTime.now());
        application.setTargetConfirmOpinion(opinion);

        if (confirmStatus == 2) {
            application.setSwapStatus(3);
        } else if (confirmStatus == 1) {
            application.setSwapStatus(1);
        }

        return updateById(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Long id, Integer approvalStatus, String opinion) {
        ShiftSwapApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("调班申请不存在");
        }
        if (application.getSwapStatus() != 1) {
            throw new BusinessException("当前状态不可审批");
        }

        application.setApprovalStatus(approvalStatus);
        application.setApprovalTime(LocalDateTime.now());
        application.setApprovalOpinion(opinion);

        if (approvalStatus == 1) {
            application.setSwapStatus(2);
            application.setEffectiveTime(LocalDateTime.now());
            executeSwap(application);
        } else if (approvalStatus == 2) {
            application.setSwapStatus(3);
        }

        return updateById(application);
    }

    private void executeSwap(ShiftSwapApplication application) {
        ScheduleDetail applicantDetail = scheduleDetailService.getById(application.getApplicantScheduleId());
        ScheduleDetail targetDetail = null;
        if (application.getTargetScheduleId() != null) {
            targetDetail = scheduleDetailService.getById(application.getTargetScheduleId());
        }

        if (applicantDetail != null && targetDetail != null) {
            Long tempEmployeeId = applicantDetail.getEmployeeId();
            String tempEmployeeName = applicantDetail.getEmployeeName();
            String tempEmployeeNo = applicantDetail.getEmployeeNo();

            applicantDetail.setEmployeeId(targetDetail.getEmployeeId());
            applicantDetail.setEmployeeName(targetDetail.getEmployeeName());
            applicantDetail.setEmployeeNo(targetDetail.getEmployeeNo());
            applicantDetail.setScheduleSource("SWAP");

            targetDetail.setEmployeeId(tempEmployeeId);
            targetDetail.setEmployeeName(tempEmployeeName);
            targetDetail.setEmployeeNo(tempEmployeeNo);
            targetDetail.setScheduleSource("SWAP");

            scheduleDetailService.updateById(applicantDetail);
            scheduleDetailService.updateById(targetDetail);
        }
    }

    @Override
    public boolean cancelApplication(Long id) {
        ShiftSwapApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("调班申请不存在");
        }
        if (application.getSwapStatus() >= 2) {
            throw new BusinessException("已生效的调班不可取消");
        }

        application.setSwapStatus(4);
        application.setCancelTime(LocalDateTime.now());
        return updateById(application);
    }

    @Override
    public ShiftSwapApplication getDetail(Long id) {
        return getById(id);
    }
}
