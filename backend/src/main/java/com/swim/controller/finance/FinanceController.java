package com.swim.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swim.common.PageQuery;
import com.swim.common.Result;
import com.swim.entity.finance.*;
import com.swim.service.finance.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
@Tag(name = "应收款&预存台账管理")
public class FinanceController {

    private final ReceivableBillService receivableBillService;
    private final ReceivablePaymentService receivablePaymentService;
    private final PrepaymentAccountService prepaymentAccountService;
    private final PrepaymentAccountLogService prepaymentAccountLogService;
    private final DepositOrderService depositOrderService;
    private final ReceivableSettlementService receivableSettlementService;

    @GetMapping("/receivable-bill/page")
    public Result<Page<ReceivableBill>> getBillPage(PageQuery query,
                                                     @RequestParam(required = false) String customerType,
                                                     @RequestParam(required = false) Long customerId,
                                                     @RequestParam(required = false) Integer billStatus,
                                                     @RequestParam(required = false) String billType,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(receivableBillService.getBillPage(query, customerType, customerId, billStatus, billType, startDate, endDate));
    }

    @GetMapping("/receivable-bill/{id}")
    public Result<ReceivableBill> getBill(@PathVariable Long id) {
        return Result.success(receivableBillService.getById(id));
    }

    @PostMapping("/receivable-bill")
    public Result<ReceivableBill> createBill(@RequestBody ReceivableBill bill) {
        return Result.success(receivableBillService.createBill(bill));
    }

    @PutMapping("/receivable-bill")
    public Result<Void> updateBill(@RequestBody ReceivableBill bill) {
        receivableBillService.updateById(bill);
        return Result.success();
    }

    @PostMapping("/receivable-bill/remind/{id}")
    public Result<Void> remindBill(@PathVariable Long id) {
        receivableBillService.remind(id);
        return Result.success();
    }

    @GetMapping("/receivable-bill/overdue")
    public Result<List<ReceivableBill>> getOverdueBills() {
        return Result.success(receivableBillService.getOverdueBills());
    }

    @GetMapping("/receivable-bill/total")
    public Result<BigDecimal> getTotalReceivable(@RequestParam(required = false) String customerType,
                                                  @RequestParam(required = false) Long customerId) {
        return Result.success(receivableBillService.getTotalReceivable(customerType, customerId));
    }

    @GetMapping("/receivable-payment/page")
    public Result<Page<ReceivablePayment>> getPaymentPage(PageQuery query,
                                                           @RequestParam(required = false) Long billId,
                                                           @RequestParam(required = false) Long customerId,
                                                           @RequestParam(required = false) Integer paymentStatus,
                                                           @RequestParam(required = false) String paymentType,
                                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(receivablePaymentService.getPaymentPage(query, billId, customerId, paymentStatus, paymentType, startDate, endDate));
    }

    @GetMapping("/receivable-payment/list/{billId}")
    public Result<List<ReceivablePayment>> getPaymentList(@PathVariable Long billId) {
        return Result.success(receivablePaymentService.getByBillId(billId));
    }

    @PostMapping("/receivable-payment")
    public Result<ReceivablePayment> createPayment(@RequestBody ReceivablePayment payment) {
        return Result.success(receivablePaymentService.createPayment(payment));
    }

    @PostMapping("/receivable-payment/confirm/{id}")
    public Result<Void> confirmPayment(@PathVariable Long id) {
        receivablePaymentService.confirmPayment(id);
        return Result.success();
    }

    @PostMapping("/receivable-payment/refund/{id}")
    public Result<Void> refundPayment(@PathVariable Long id,
                                       @RequestParam BigDecimal refundAmount,
                                       @RequestParam(required = false) String remark) {
        receivablePaymentService.refundPayment(id, refundAmount, remark);
        return Result.success();
    }

    @GetMapping("/prepayment-account/page")
    public Result<Page<PrepaymentAccount>> getAccountPage(PageQuery query,
                                                           @RequestParam(required = false) String accountType,
                                                           @RequestParam(required = false) String customerType,
                                                           @RequestParam(required = false) Long customerId,
                                                           @RequestParam(required = false) Integer status,
                                                           @RequestParam(required = false) String keyword) {
        return Result.success(prepaymentAccountService.getAccountPage(query, accountType, customerType, customerId, status, keyword));
    }

    @GetMapping("/prepayment-account/{id}")
    public Result<PrepaymentAccount> getAccount(@PathVariable Long id) {
        return Result.success(prepaymentAccountService.getById(id));
    }

    @PostMapping("/prepayment-account")
    public Result<PrepaymentAccount> createAccount(@RequestBody PrepaymentAccount account) {
        return Result.success(prepaymentAccountService.createAccount(account));
    }

    @PostMapping("/prepayment-account/recharge/{id}")
    public Result<Void> recharge(@PathVariable Long id,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam(required = false) BigDecimal giftAmount,
                                  @RequestParam(required = false) String businessType,
                                  @RequestParam(required = false) Long businessId,
                                  @RequestParam(required = false) String businessNo,
                                  @RequestParam(required = false) Long operatorId,
                                  @RequestParam(required = false) String operatorName,
                                  @RequestParam(required = false) String remark) {
        prepaymentAccountService.recharge(id, amount, giftAmount, businessType, businessId, businessNo, operatorId, operatorName, remark);
        return Result.success();
    }

    @PostMapping("/prepayment-account/consume/{id}")
    public Result<Void> consume(@PathVariable Long id,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam(defaultValue = "PRINCIPAL") String balanceType,
                                 @RequestParam(required = false) String businessType,
                                 @RequestParam(required = false) Long businessId,
                                 @RequestParam(required = false) String businessNo,
                                 @RequestParam(required = false) Long operatorId,
                                 @RequestParam(required = false) String operatorName,
                                 @RequestParam(required = false) String remark) {
        prepaymentAccountService.consume(id, amount, balanceType, businessType, businessId, businessNo, operatorId, operatorName, remark);
        return Result.success();
    }

    @PostMapping("/prepayment-account/freeze/{id}")
    public Result<Void> freeze(@PathVariable Long id,
                                @RequestParam BigDecimal amount,
                                @RequestParam(required = false) String remark) {
        prepaymentAccountService.freeze(id, amount, remark);
        return Result.success();
    }

    @PostMapping("/prepayment-account/unfreeze/{id}")
    public Result<Void> unfreeze(@PathVariable Long id,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam(required = false) String remark) {
        prepaymentAccountService.unfreeze(id, amount, remark);
        return Result.success();
    }

    @GetMapping("/prepayment-log/page")
    public Result<Page<PrepaymentAccountLog>> getLogPage(PageQuery query,
                                                          @RequestParam(required = false) Long accountId,
                                                          @RequestParam(required = false) String changeType,
                                                          @RequestParam(required = false) String businessType) {
        return Result.success(prepaymentAccountLogService.getLogPage(query, accountId, changeType, businessType, null, null));
    }

    @GetMapping("/prepayment-log/list/{accountId}")
    public Result<List<PrepaymentAccountLog>> getLogList(@PathVariable Long accountId) {
        return Result.success(prepaymentAccountLogService.getByAccountId(accountId));
    }

    @GetMapping("/deposit/page")
    public Result<Page<DepositOrder>> getDepositPage(PageQuery query,
                                                      @RequestParam(required = false) String customerType,
                                                      @RequestParam(required = false) Long customerId,
                                                      @RequestParam(required = false) String businessType,
                                                      @RequestParam(required = false) Integer depositStatus,
                                                      @RequestParam(required = false) Integer payStatus) {
        return Result.success(depositOrderService.getDepositPage(query, customerType, customerId, businessType, depositStatus, payStatus));
    }

    @GetMapping("/deposit/{id}")
    public Result<DepositOrder> getDeposit(@PathVariable Long id) {
        return Result.success(depositOrderService.getById(id));
    }

    @PostMapping("/deposit")
    public Result<DepositOrder> createDeposit(@RequestBody DepositOrder deposit) {
        return Result.success(depositOrderService.createDeposit(deposit));
    }

    @PostMapping("/deposit/pay/{id}")
    public Result<Void> payDeposit(@PathVariable Long id, @RequestParam String payType) {
        depositOrderService.payDeposit(id, payType);
        return Result.success();
    }

    @PostMapping("/deposit/deduct/{id}")
    public Result<Void> deductDeposit(@PathVariable Long id,
                                       @RequestParam BigDecimal deductAmount,
                                       @RequestParam(required = false) String businessType,
                                       @RequestParam(required = false) Long businessId,
                                       @RequestParam(required = false) String businessNo) {
        depositOrderService.deductDeposit(id, deductAmount, businessType, businessId, businessNo);
        return Result.success();
    }

    @PostMapping("/deposit/refund/{id}")
    public Result<Void> refundDeposit(@PathVariable Long id,
                                       @RequestParam BigDecimal refundAmount,
                                       @RequestParam(required = false) String reason) {
        depositOrderService.refundDeposit(id, refundAmount, reason);
        return Result.success();
    }

    @PostMapping("/deposit/void/{id}")
    public Result<Void> voidDeposit(@PathVariable Long id, @RequestParam(required = false) String reason) {
        depositOrderService.voidDeposit(id, reason);
        return Result.success();
    }

    @GetMapping("/deposit/valid")
    public Result<List<DepositOrder>> getValidDeposits(@RequestParam(required = false) String customerType,
                                                        @RequestParam(required = false) Long customerId) {
        return Result.success(depositOrderService.getValidDeposits(customerType, customerId));
    }

    @GetMapping("/settlement/page")
    public Result<Page<ReceivableSettlement>> getSettlementPage(PageQuery query,
                                                                  @RequestParam(required = false) String customerType,
                                                                  @RequestParam(required = false) Long customerId,
                                                                  @RequestParam(required = false) String settlementPeriod,
                                                                  @RequestParam(required = false) Integer settlementStatus) {
        return Result.success(receivableSettlementService.getSettlementPage(query, customerType, customerId, settlementPeriod, settlementStatus));
    }

    @GetMapping("/settlement/{id}")
    public Result<ReceivableSettlement> getSettlement(@PathVariable Long id) {
        return Result.success(receivableSettlementService.getById(id));
    }

    @PostMapping("/settlement/generate")
    public Result<ReceivableSettlement> generateSettlement(@RequestParam(required = false) String customerType,
                                                            @RequestParam(required = false) Long customerId,
                                                            @RequestParam String settlementPeriod) {
        return Result.success(receivableSettlementService.generateSettlement(customerType, customerId, settlementPeriod));
    }

    @PostMapping("/settlement/confirm/{id}")
    public Result<Void> confirmSettlement(@PathVariable Long id) {
        receivableSettlementService.confirmSettlement(id);
        return Result.success();
    }

    @PostMapping("/settlement/object/{id}")
    public Result<Void> objectSettlement(@PathVariable Long id, @RequestParam(required = false) String remark) {
        receivableSettlementService.objectSettlement(id, remark);
        return Result.success();
    }
}
