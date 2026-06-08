package com.swim.service.system.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.system.ElectronicAgreementTemplate;
import com.swim.mapper.system.ElectronicAgreementTemplateMapper;
import com.swim.service.system.ElectronicAgreementTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ElectronicAgreementTemplateServiceImpl extends ServiceImpl<ElectronicAgreementTemplateMapper, ElectronicAgreementTemplate> implements ElectronicAgreementTemplateService {

    @Override
    public Page<ElectronicAgreementTemplate> getTemplatePage(PageQuery query, String agreementType, Integer status) {
        Page<ElectronicAgreementTemplate> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ElectronicAgreementTemplate> wrapper = new LambdaQueryWrapper<>();
        if (agreementType != null && !agreementType.isEmpty()) {
            wrapper.eq(ElectronicAgreementTemplate::getAgreementType, agreementType);
        }
        if (status != null) {
            wrapper.eq(ElectronicAgreementTemplate::getStatus, status);
        }
        wrapper.orderByDesc(ElectronicAgreementTemplate::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public ElectronicAgreementTemplate getCurrent(String agreementType) {
        LambdaQueryWrapper<ElectronicAgreementTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ElectronicAgreementTemplate::getAgreementType, agreementType);
        wrapper.eq(ElectronicAgreementTemplate::getIsCurrent, 1);
        wrapper.eq(ElectronicAgreementTemplate::getStatus, 1);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ElectronicAgreementTemplate createTemplate(ElectronicAgreementTemplate template) {
        template.setTemplateNo("EAT" + IdUtil.getSnowflakeNextIdStr());
        if (template.getVersion() == null) {
            template.setVersion("1.0");
        }
        if (template.getStatus() == null) {
            template.setStatus(1);
        }
        if (template.getIsCurrent() == null) {
            template.setIsCurrent(0);
        }
        save(template);
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setCurrent(Long id) {
        ElectronicAgreementTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("协议模板不存在");
        }
        if (template.getStatus() != 1) {
            throw new BusinessException("只有启用状态的模板可以设为当前版本");
        }

        LambdaQueryWrapper<ElectronicAgreementTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ElectronicAgreementTemplate::getAgreementType, template.getAgreementType());
        wrapper.eq(ElectronicAgreementTemplate::getIsCurrent, 1);
        List<ElectronicAgreementTemplate> currentList = list(wrapper);
        for (ElectronicAgreementTemplate t : currentList) {
            t.setIsCurrent(0);
            updateById(t);
        }

        template.setIsCurrent(1);
        return updateById(template);
    }

    @Override
    public List<ElectronicAgreementTemplate> getByType(String agreementType) {
        LambdaQueryWrapper<ElectronicAgreementTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ElectronicAgreementTemplate::getAgreementType, agreementType);
        wrapper.orderByDesc(ElectronicAgreementTemplate::getCreateTime);
        return list(wrapper);
    }
}
