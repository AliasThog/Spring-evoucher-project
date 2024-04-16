package com.example.evoucherproject.service;

import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.entity.Voucher;
import org.springframework.http.ResponseEntity;

public interface VoucherService {
    CustomResponse saveVoucher(int customerId, int voucherCategoryId,int discount);
}
