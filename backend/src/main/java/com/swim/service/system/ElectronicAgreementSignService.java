package com.swim.service.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.system.ElectronicAgreementSign;

import java.util.List;

public interface ElectronicAgreementSignService extends IService<ElectronicAgreementSign> {

    Page<ElectronicAgreementSign> getSignPage(PageQuery query, String agreementType, Integer signStatus,
                                              String signerType, Long signerId, String businessType, Long businessId);

    ElectronicAgreementSign initiateSign(String agreementType, String businessType, Long businessId,
                                         String businessNo, String signerType, Long signerId,
                                         String signerName, String signerIdCard, String signerPhone,
                                         Long operatorId, String operatorName);

    boolean sign(Long id, String signIp, String signLocation, String signatureImage,
                 String verifyCode, Long operatorId, String operatorName);

    boolean reject(Long id, String reason);

    boolean voidSign(Long id, String reason);

    List<ElectronicAgreementSign> getByBusiness(String businessType, Long businessId);
}
