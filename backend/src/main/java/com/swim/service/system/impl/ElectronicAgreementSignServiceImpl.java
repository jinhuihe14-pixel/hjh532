package com.swim.service.system.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.common.PageQuery;
import com.swim.entity.system.ElectronicAgreementSign;
import com.swim.entity.system.ElectronicAgreementTemplate;
import com.swim.mapper.system.ElectronicAgreementSignMapper;
import com.swim.service.system.ElectronicAgreementSignService;
import com.swim.service.system.ElectronicAgreementTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectronicAgreementSignServiceImpl extends ServiceImpl<ElectronicAgreementSignMapper, ElectronicAgreementSign> implements ElectronicAgreementSignService {

    private final ElectronicAgreementTemplateService templateService;

    @Override
    public Page<ElectronicAgreementSign> getSignPage(PageQuery query, String agreementType, Integer signStatus,
                                                     String signerType, Long signerId, String businessType, Long businessId) {
        Page<ElectronicAgreementSign> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ElectronicAgreementSign> wrapper = new LambdaQueryWrapper<>();
        if (agreementType != null && !agreementType.isEmpty()) {
            wrapper.eq(ElectronicAgreementSign::getAgreementType, agreementType);
        }
        if (signStatus != null) {
            wrapper.eq(ElectronicAgreementSign::getSignStatus, signStatus);
        }
        if (signerType != null && !signerType.isEmpty()) {
            wrapper.eq(ElectronicAgreementSign::getSignerType, signerType);
        }
        if (signerId != null) {
            wrapper.eq(ElectronicAgreementSign::getSignerId, signerId);
        }
        if (businessType != null && !businessType.isEmpty()) {
            wrapper.eq(ElectronicAgreementSign::getBusinessType, businessType);
        }
        if (businessId != null) {
            wrapper.eq(ElectronicAgreementSign::getBusinessId, businessId);
        }
        wrapper.orderByDesc(ElectronicAgreementSign::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ElectronicAgreementSign initiateSign(String agreementType, String businessType, Long businessId,
                                                 String businessNo, String signerType, Long signerId,
                                                 String signerName, String signerIdCard, String signerPhone,
                                                 Long operatorId, String operatorName) {
        ElectronicAgreementTemplate template = templateService.getCurrent(agreementType);
        if (template == null) {
            throw new BusinessException("未找到当前生效的协议模板");
        }

        ElectronicAgreementSign sign = new ElectronicAgreementSign();
        sign.setSignNo("EAS" + IdUtil.getSnowflakeNextIdStr());
        sign.setTemplateId(template.getId());
        sign.setTemplateNo(template.getTemplateNo());
        sign.setTemplateName(template.getTemplateName());
        sign.setAgreementType(agreementType);
        sign.setAgreementVersion(template.getVersion());
        sign.setAgreementSnapshot(template.getAgreementContent());
        sign.setBusinessType(businessType);
        sign.setBusinessId(businessId);
        sign.setBusinessNo(businessNo);
        sign.setSignerType(signerType);
        sign.setSignerId(signerId);
        sign.setSignerName(signerName);
        sign.setSignerIdCard(signerIdCard);
        sign.setSignerPhone(signerPhone);
        sign.setSignStatus(0);
        sign.setIsAutoSign(0);
        sign.setOperatorId(operatorId);
        sign.setOperatorName(operatorName);
        save(sign);

        return sign;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sign(Long id, String signIp, String signLocation, String signatureImage,
                        String verifyCode, Long operatorId, String operatorName) {
        ElectronicAgreementSign sign = getById(id);
        if (sign == null) {
            throw new BusinessException("签署记录不存在");
        }
        if (sign.getSignStatus() != 0) {
            throw new BusinessException("只有待签署状态的协议可以签署");
        }

        sign.setSignStatus(1);
        sign.setSignTime(LocalDateTime.now());
        sign.setSignIp(signIp);
        sign.setSignLocation(signLocation);
        sign.setSignatureImage(signatureImage);
        sign.setVerifyCode(verifyCode);
        sign.setVerifyTime(LocalDateTime.now());

        return updateById(sign);
    }

    @Override
    public boolean reject(Long id, String reason) {
        ElectronicAgreementSign sign = getById(id);
        if (sign == null) {
            throw new BusinessException("签署记录不存在");
        }
        if (sign.getSignStatus() != 0) {
            throw new BusinessException("只有待签署状态的协议可以拒绝");
        }
        sign.setSignStatus(2);
        sign.setRemark(reason);
        return updateById(sign);
    }

    @Override
    public boolean voidSign(Long id, String reason) {
        ElectronicAgreementSign sign = getById(id);
        if (sign == null) {
            throw new BusinessException("签署记录不存在");
        }
        sign.setSignStatus(3);
        sign.setRemark(reason);
        return updateById(sign);
    }

    @Override
    public List<ElectronicAgreementSign> getByBusiness(String businessType, Long businessId) {
        LambdaQueryWrapper<ElectronicAgreementSign> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ElectronicAgreementSign::getBusinessType, businessType);
        wrapper.eq(ElectronicAgreementSign::getBusinessId, businessId);
        wrapper.orderByDesc(ElectronicAgreementSign::getCreateTime);
        return list(wrapper);
    }
}
