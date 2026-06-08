package com.swim.mapper.ticket;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.ticket.TicketOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketOrderMapper extends BaseMapper<TicketOrder> {
}
