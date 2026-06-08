package com.swim.service.group;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupCustomer;

import java.util.List;

public interface GroupCustomerService extends IService<GroupCustomer> {

    Page<GroupCustomer> getCustomerPage(PageQuery query, String customerType, String customerLevel, String keyword);

    GroupCustomer getByNo(String customerNo);

    boolean addCustomer(GroupCustomer customer);

    boolean updateCustomer(GroupCustomer customer);

    List<GroupCustomer> getActiveList();
}
