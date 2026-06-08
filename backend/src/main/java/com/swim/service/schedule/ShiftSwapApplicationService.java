package com.swim.service.schedule;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.schedule.ShiftSwapApplication;

public interface ShiftSwapApplicationService extends IService<ShiftSwapApplication> {

    Page<ShiftSwapApplication> getApplicationPage(PageQuery query, Long employeeId, Integer swapStatus);

    boolean applySwap(ShiftSwapApplication application);

    boolean confirmByTarget(Long id, Integer confirmStatus, String opinion);

    boolean approve(Long id, Integer approvalStatus, String opinion);

    boolean cancelApplication(Long id);

    ShiftSwapApplication getDetail(Long id);
}
