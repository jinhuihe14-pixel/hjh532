package com.swim.service.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.finance.ReceivableSettlement;

import java.util.List;

public interface ReceivableSettlementService extends IService<ReceivableSettlement> {

    Page<ReceivableSettlement> getSettlementPage(PageQuery query, String customerType, Long customerId,
                                                  String settlementPeriod, Integer settlementStatus);

    ReceivableSettlement generateSettlement(String customerType, Long customerId, String settlementPeriod);

    boolean confirmSettlement(Long id);

    boolean objectSettlement(Long id, String remark);

    List<ReceivableSettlement> getByCustomer(String customerType, Long customerId);

    ReceivableSettlement getByPeriod(String customerType, Long customerId, String settlementPeriod);
}
