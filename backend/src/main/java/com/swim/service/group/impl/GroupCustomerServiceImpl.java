package com.swim.service.group.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupCustomer;
import com.swim.mapper.group.GroupCustomerMapper;
import com.swim.service.group.GroupCustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupCustomerServiceImpl extends ServiceImpl<GroupCustomerMapper, GroupCustomer> implements GroupCustomerService {

    @Override
    public Page<GroupCustomer> getCustomerPage(PageQuery query, String customerType, String customerLevel, String keyword) {
        Page<GroupCustomer> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GroupCustomer> wrapper = new LambdaQueryWrapper<>();
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(GroupCustomer::getCustomerType, customerType);
        }
        if (customerLevel != null && !customerLevel.isEmpty()) {
            wrapper.eq(GroupCustomer::getCustomerLevel, customerLevel);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(GroupCustomer::getCustomerName, keyword)
                    .or().like(GroupCustomer::getContactName, keyword)
                    .or().like(GroupCustomer::getContactPhone, keyword);
        }
        wrapper.orderByDesc(GroupCustomer::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public GroupCustomer getByNo(String customerNo) {
        LambdaQueryWrapper<GroupCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupCustomer::getCustomerNo, customerNo);
        return getOne(wrapper);
    }

    @Override
    public boolean addCustomer(GroupCustomer customer) {
        customer.setCustomerNo("GC" + IdUtil.getSnowflakeNextIdStr());
        if (customer.getStatus() == null) {
            customer.setStatus(1);
        }
        return save(customer);
    }

    @Override
    public boolean updateCustomer(GroupCustomer customer) {
        GroupCustomer exist = getById(customer.getId());
        if (exist == null) {
            throw new BusinessException("客户不存在");
        }
        return updateById(customer);
    }

    @Override
    public List<GroupCustomer> getActiveList() {
        LambdaQueryWrapper<GroupCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupCustomer::getStatus, 1);
        wrapper.orderByAsc(GroupCustomer::getCustomerName);
        return list(wrapper);
    }
}
