package com.swim.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("electronic_agreement_sign")
public class ElectronicAgreementSign extends BaseEntity {

    private String signNo;

    private Long templateId;

    private String templateNo;

    private String templateName;

    private String agreementType;

    private String agreementVersion;

    private String agreementSnapshot;

    private String businessType;

    private Long businessId;

    private String businessNo;

    private String signerType;

    private Long signerId;

    private String signerName;

    private String signerIdCard;

    private String signerPhone;

    private Integer signStatus;

    private LocalDateTime signTime;

    private String signIp;

    private String signLocation;

    private String signatureImage;

    private Integer isAutoSign;

    private String verifyCode;

    private LocalDateTime verifyTime;

    private Long operatorId;

    private String operatorName;

    private String remark;
}
