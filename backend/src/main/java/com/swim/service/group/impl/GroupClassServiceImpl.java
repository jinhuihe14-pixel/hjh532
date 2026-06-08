package com.swim.service.group.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupClass;
import com.swim.mapper.group.GroupClassMapper;
import com.swim.service.group.GroupClassService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupClassServiceImpl extends ServiceImpl<GroupClassMapper, GroupClass> implements GroupClassService {

    @Override
    public Page<GroupClass> getClassPage(PageQuery query, Long customerId, Integer classStatus, String courseType) {
        Page<GroupClass> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GroupClass> wrapper = new LambdaQueryWrapper<>();
        if (customerId != null) {
            wrapper.eq(GroupClass::getCustomerId, customerId);
        }
        if (classStatus != null) {
            wrapper.eq(GroupClass::getClassStatus, classStatus);
        }
        if (courseType != null && !courseType.isEmpty()) {
            wrapper.eq(GroupClass::getCourseType, courseType);
        }
        wrapper.orderByDesc(GroupClass::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public GroupClass createClass(GroupClass groupClass) {
        groupClass.setClassNo("GCLS" + IdUtil.getSnowflakeNextIdStr());
        groupClass.setClassStatus(1);
        groupClass.setCompletedSessions(0);
        if (groupClass.getStudentCount() == null) {
            groupClass.setStudentCount(0);
        }
        save(groupClass);
        return groupClass;
    }

    @Override
    public boolean cancelClass(Long id) {
        GroupClass groupClass = getById(id);
        if (groupClass == null) {
            throw new BusinessException("团体课程不存在");
        }
        groupClass.setClassStatus(0);
        return updateById(groupClass);
    }

    @Override
    public boolean finishClass(Long id) {
        GroupClass groupClass = getById(id);
        if (groupClass == null) {
            throw new BusinessException("团体课程不存在");
        }
        groupClass.setClassStatus(3);
        return updateById(groupClass);
    }

    @Override
    public List<GroupClass> getByCustomerId(Long customerId) {
        LambdaQueryWrapper<GroupClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupClass::getCustomerId, customerId);
        wrapper.orderByDesc(GroupClass::getCreateTime);
        return list(wrapper);
    }
}
