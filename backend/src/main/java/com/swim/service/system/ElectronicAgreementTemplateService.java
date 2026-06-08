package com.swim.service.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.system.ElectronicAgreementTemplate;

import java.util.List;

public interface ElectronicAgreementTemplateService extends IService<ElectronicAgreementTemplate> {

    Page<ElectronicAgreementTemplate> getTemplatePage(PageQuery query, String agreementType, Integer status);

    ElectronicAgreementTemplate getCurrent(String agreementType);

    ElectronicAgreementTemplate createTemplate(ElectronicAgreementTemplate template);

    boolean setCurrent(Long id);

    List<ElectronicAgreementTemplate> getByType(String agreementType);
}
