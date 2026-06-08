package com.swim.mapper.finance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.finance.DepositOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepositOrderMapper extends BaseMapper<DepositOrder> {
}
