package com.swim.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swim.common.PageQuery;
import com.swim.common.Result;
import com.swim.entity.system.*;
import com.swim.service.system.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
@Tag(name = "系统管理-优化功能")
public class SystemOptController {

    private final SysOperationLogService operationLogService;
    private final ElectronicAgreementTemplateService agreementTemplateService;
    private final ElectronicAgreementSignService agreementSignService;
    private final VenueOvertimeRuleService overtimeRuleService;
    private final VenueOvertimeFeeService overtimeFeeService;
    private final SysFeatureSwitchService featureSwitchService;

    @GetMapping("/operation-log/page")
    public Result<Page<SysOperationLog>> getLogPage(PageQuery query,
                                                     @RequestParam(required = false) String moduleType,
                                                     @RequestParam(required = false) String businessType,
                                                     @RequestParam(required = false) Long operatorId,
                                                     @RequestParam(required = false) String keyword) {
        return Result.success(operationLogService.getLogPage(query, moduleType, businessType, operatorId, keyword, null, null));
    }

    @GetMapping("/operation-log/{id}")
    public Result<SysOperationLog> getLogDetail(@PathVariable Long id) {
        return Result.success(operationLogService.getDetail(id));
    }

    @GetMapping("/agreement-template/page")
    public Result<Page<ElectronicAgreementTemplate>> getTemplatePage(PageQuery query,
                                                                      @RequestParam(required = false) String agreementType,
                                                                      @RequestParam(required = false) Integer status) {
        return Result.success(agreementTemplateService.getTemplatePage(query, agreementType, status));
    }

    @GetMapping("/agreement-template/current")
    public Result<ElectronicAgreementTemplate> getCurrentTemplate(@RequestParam String agreementType) {
        return Result.success(agreementTemplateService.getCurrent(agreementType));
    }

    @GetMapping("/agreement-template/{id}")
    public Result<ElectronicAgreementTemplate> getTemplate(@PathVariable Long id) {
        return Result.success(agreementTemplateService.getById(id));
    }

    @PostMapping("/agreement-template")
    public Result<ElectronicAgreementTemplate> createTemplate(@RequestBody ElectronicAgreementTemplate template) {
        return Result.success(agreementTemplateService.createTemplate(template));
    }

    @PutMapping("/agreement-template")
    public Result<Void> updateTemplate(@RequestBody ElectronicAgreementTemplate template) {
        agreementTemplateService.updateById(template);
        return Result.success();
    }

    @PostMapping("/agreement-template/set-current/{id}")
    public Result<Void> setCurrentTemplate(@PathVariable Long id) {
        agreementTemplateService.setCurrent(id);
        return Result.success();
    }

    @GetMapping("/agreement-sign/page")
    public Result<Page<ElectronicAgreementSign>> getSignPage(PageQuery query,
                                                              @RequestParam(required = false) String agreementType,
                                                              @RequestParam(required = false) Integer signStatus,
                                                              @RequestParam(required = false) String signerType,
                                                              @RequestParam(required = false) Long signerId,
                                                              @RequestParam(required = false) String businessType,
                                                              @RequestParam(required = false) Long businessId) {
        return Result.success(agreementSignService.getSignPage(query, agreementType, signStatus, signerType, signerId, businessType, businessId));
    }

    @GetMapping("/agreement-sign/{id}")
    public Result<ElectronicAgreementSign> getSignDetail(@PathVariable Long id) {
        return Result.success(agreementSignService.getById(id));
    }

    @PostMapping("/agreement-sign/initiate")
    public Result<ElectronicAgreementSign> initiateSign(@RequestParam String agreementType,
                                                         @RequestParam String businessType,
                                                         @RequestParam Long businessId,
                                                         @RequestParam(required = false) String businessNo,
                                                         @RequestParam String signerType,
                                                         @RequestParam(required = false) Long signerId,
                                                         @RequestParam String signerName,
                                                         @RequestParam(required = false) String signerIdCard,
                                                         @RequestParam(required = false) String signerPhone,
                                                         @RequestParam(required = false) Long operatorId,
                                                         @RequestParam(required = false) String operatorName) {
        return Result.success(agreementSignService.initiateSign(agreementType, businessType, businessId, businessNo,
                signerType, signerId, signerName, signerIdCard, signerPhone, operatorId, operatorName));
    }

    @PostMapping("/agreement-sign/sign/{id}")
    public Result<Void> signAgreement(@PathVariable Long id,
                                       @RequestParam(required = false) String signIp,
                                       @RequestParam(required = false) String signLocation,
                                       @RequestParam(required = false) String signatureImage,
                                       @RequestParam(required = false) String verifyCode,
                                       @RequestParam(required = false) Long operatorId,
                                       @RequestParam(required = false) String operatorName) {
        agreementSignService.sign(id, signIp, signLocation, signatureImage, verifyCode, operatorId, operatorName);
        return Result.success();
    }

    @PostMapping("/agreement-sign/reject/{id}")
    public Result<Void> rejectAgreement(@PathVariable Long id, @RequestParam(required = false) String reason) {
        agreementSignService.reject(id, reason);
        return Result.success();
    }

    @PostMapping("/agreement-sign/void/{id}")
    public Result<Void> voidAgreement(@PathVariable Long id, @RequestParam(required = false) String reason) {
        agreementSignService.voidSign(id, reason);
        return Result.success();
    }

    @GetMapping("/overtime-rule/page")
    public Result<Page<VenueOvertimeRule>> getOvertimeRulePage(PageQuery query,
                                                                @RequestParam(required = false) String venueType,
                                                                @RequestParam(required = false) Long venueId,
                                                                @RequestParam(required = false) Integer status) {
        return Result.success(overtimeRuleService.getRulePage(query, venueType, venueId, status));
    }

    @GetMapping("/overtime-rule/{id}")
    public Result<VenueOvertimeRule> getOvertimeRule(@PathVariable Long id) {
        return Result.success(overtimeRuleService.getById(id));
    }

    @PostMapping("/overtime-rule")
    public Result<VenueOvertimeRule> createOvertimeRule(@RequestBody VenueOvertimeRule rule) {
        return Result.success(overtimeRuleService.createRule(rule));
    }

    @PutMapping("/overtime-rule")
    public Result<Void> updateOvertimeRule(@RequestBody VenueOvertimeRule rule) {
        overtimeRuleService.updateRule(rule);
        return Result.success();
    }

    @DeleteMapping("/overtime-rule/{id}")
    public Result<Void> deleteOvertimeRule(@PathVariable Long id) {
        overtimeRuleService.removeById(id);
        return Result.success();
    }

    @GetMapping("/overtime-fee/page")
    public Result<Page<VenueOvertimeFee>> getOvertimeFeePage(PageQuery query,
                                                              @RequestParam(required = false) Long rentalOrderId,
                                                              @RequestParam(required = false) Long venueId,
                                                              @RequestParam(required = false) Integer payStatus,
                                                              @RequestParam(required = false) Integer feeStatus) {
        return Result.success(overtimeFeeService.getFeePage(query, rentalOrderId, venueId, payStatus, feeStatus, null, null));
    }

    @GetMapping("/overtime-fee/{id}")
    public Result<VenueOvertimeFee> getOvertimeFee(@PathVariable Long id) {
        return Result.success(overtimeFeeService.getById(id));
    }

    @PostMapping("/overtime-fee/pay/{id}")
    public Result<Void> payOvertimeFee(@PathVariable Long id,
                                        @RequestParam String payType,
                                        @RequestParam(required = false) Long deductionAccountId,
                                        @RequestParam(required = false) Long operatorId,
                                        @RequestParam(required = false) String operatorName) {
        overtimeFeeService.payFee(id, payType, deductionAccountId, operatorId, operatorName);
        return Result.success();
    }

    @PostMapping("/overtime-fee/waive/{id}")
    public Result<Void> waiveOvertimeFee(@PathVariable Long id,
                                          @RequestParam(required = false) String reason,
                                          @RequestParam(required = false) Long operatorId,
                                          @RequestParam(required = false) String operatorName) {
        overtimeFeeService.waiveFee(id, reason, operatorId, operatorName);
        return Result.success();
    }

    @GetMapping("/feature-switch/list")
    public Result<List<SysFeatureSwitch>> getFeatureSwitchList() {
        return Result.success(featureSwitchService.getAllSwitches());
    }

    @GetMapping("/feature-switch/module")
    public Result<List<SysFeatureSwitch>> getByModule(@RequestParam String featureModule) {
        return Result.success(featureSwitchService.getByModule(featureModule));
    }

    @GetMapping("/feature-switch/enabled")
    public Result<Boolean> isFeatureEnabled(@RequestParam String featureCode) {
        return Result.success(featureSwitchService.isEnabled(featureCode));
    }

    @PostMapping("/feature-switch/enable/{featureCode}")
    public Result<Void> enableFeature(@PathVariable String featureCode) {
        featureSwitchService.enableFeature(featureCode);
        return Result.success();
    }

    @PostMapping("/feature-switch/disable/{featureCode}")
    public Result<Void> disableFeature(@PathVariable String featureCode) {
        featureSwitchService.disableFeature(featureCode);
        return Result.success();
    }

    @PutMapping("/feature-switch")
    public Result<Void> updateSwitch(@RequestBody SysFeatureSwitch featureSwitch) {
        featureSwitchService.updateSwitch(featureSwitch.getId(), featureSwitch.getIsEnabled(), featureSwitch.getRemark());
        return Result.success();
    }
}
