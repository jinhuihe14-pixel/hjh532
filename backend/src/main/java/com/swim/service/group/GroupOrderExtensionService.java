package com.swim.service.group;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.group.GroupOrderExtension;

import java.time.LocalDate;
import java.util.List;

public interface GroupOrderExtensionService extends IService<GroupOrderExtension> {

    List<GroupOrderExtension> getByOrderId(Long orderId);

    GroupOrderExtension applyExtension(Long orderId, LocalDate newEndDate, String reason);

    boolean approve(Long id, Integer approvalStatus, String opinion);
}
