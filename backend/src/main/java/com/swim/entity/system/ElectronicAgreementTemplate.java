package com.swim.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("electronic_agreement_template")
public class ElectronicAgreementTemplate extends BaseEntity {

    private String templateNo;

    private String templateName;

    private String agreementType;

    private String agreementTitle;

    private String agreementContent;

    private String version;

    private Integer isCurrent;

    private LocalDate effectiveDate;

    private LocalDate expireDate;

    private Integer status;

    private String remark;
}
