package com.swim.service.group;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupClass;

import java.util.List;

public interface GroupClassService extends IService<GroupClass> {

    Page<GroupClass> getClassPage(PageQuery query, Long customerId, Integer classStatus, String courseType);

    GroupClass createClass(GroupClass groupClass);

    boolean cancelClass(Long id);

    boolean finishClass(Long id);

    List<GroupClass> getByCustomerId(Long customerId);
}
