package com.swim.service.group.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.entity.group.GroupOrder;
import com.swim.entity.group.GroupOrderExtension;
import com.swim.mapper.group.GroupOrderExtensionMapper;
import com.swim.service.group.GroupOrderExtensionService;
import com.swim.service.group.GroupOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupOrderExtensionServiceImpl extends ServiceImpl<GroupOrderExtensionMapper, GroupOrderExtension> implements GroupOrderExtensionService {

    private final GroupOrderService groupOrderService;

    @Override
    public List<GroupOrderExtension> getByOrderId(Long orderId) {
        LambdaQueryWrapper<GroupOrderExtension> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupOrderExtension::getOrderId, orderId);
        wrapper.orderByDesc(GroupOrderExtension::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GroupOrderExtension applyExtension(Long orderId, LocalDate newEndDate, String reason) {
        GroupOrder order = groupOrderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("团单不存在");
        }
        if (order.getValidEndDate() == null) {
            throw new BusinessException("团单没有有效期设置");
        }
        if (!newEndDate.isAfter(order.getValidEndDate())) {
            throw new BusinessException("新到期日期必须晚于原到期日期");
        }

        int extensionDays = (int) ChronoUnit.DAYS.between(order.getValidEndDate(), newEndDate);

        GroupOrderExtension extension = new GroupOrderExtension();
        extension.setExtensionNo("GOE" + IdUtil.getSnowflakeNextIdStr());
        extension.setOrderId(orderId);
        extension.setOrderNo(order.getOrderNo());
        extension.setOriginalEndDate(order.getValidEndDate());
        extension.setNewEndDate(newEndDate);
        extension.setExtensionDays(extensionDays);
        extension.setExtensionFee(java.math.BigDecimal.ZERO);
        extension.setApplyReason(reason);
        extension.setApprovalStatus(0);

        save(extension);
        return extension;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Long id, Integer approvalStatus, String opinion) {
        GroupOrderExtension extension = getById(id);
        if (extension == null) {
            throw new BusinessException("延期申请不存在");
        }
        if (extension.getApprovalStatus() != 0) {
            throw new BusinessException("该申请已审批");
        }

        extension.setApprovalStatus(approvalStatus);
        extension.setApprovalTime(LocalDateTime.now());
        extension.setApprovalOpinion(opinion);

        if (approvalStatus == 1) {
            GroupOrder order = groupOrderService.getById(extension.getOrderId());
            if (order != null) {
                order.setValidEndDate(extension.getNewEndDate());
                groupOrderService.updateById(order);
            }
        }

        return updateById(extension);
    }
}
