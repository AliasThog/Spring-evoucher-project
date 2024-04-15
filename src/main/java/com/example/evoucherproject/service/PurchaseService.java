package com.example.evoucherproject.service;


import com.example.evoucherproject.model.dto.CustomResponse;

import java.util.List;

public interface PurchaseService {
    CustomResponse paymentCustomer(int customerId , int productId);
}
