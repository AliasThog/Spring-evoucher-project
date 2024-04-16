package com.example.evoucherproject.controller;

import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.service.PurchaseService;
import com.example.evoucherproject.service.VoucherService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/voucher/received")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    @Schema(description = "claim voucher with Shipping",name = "claim voucher with Shipping")
    @GetMapping("/5_percent/{customerId}")
    public ResponseEntity<CustomResponse> claimVoucherWith5PercentShipping(@PathVariable(name = "customerId"  ) int customerId) {
        return ResponseEntity.ok().body(voucherService.saveVoucher(customerId,1));
    }

    @Schema(description = "claim voucher with Discount" ,name = "claim voucher with Discount")
    @GetMapping("/3_percent/{customerId}")
    public ResponseEntity<CustomResponse> claimVoucherWith3PercentDiscount(@PathVariable(name = "customerId") int customerId) {
        return ResponseEntity.ok().body(voucherService.saveVoucher(customerId,2));
    }

}
