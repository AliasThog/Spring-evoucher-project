package com.example.evoucherproject.service.Impl;

import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.entity.Customer;
import com.example.evoucherproject.model.entity.Voucher;
import com.example.evoucherproject.repository.CustomerRepository;
import com.example.evoucherproject.repository.VoucherRepository;
import com.example.evoucherproject.service.VoucherService;
import com.example.evoucherproject.ultil.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;


@Service
public class VoucherServiceImpl implements VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomResponse saveVoucher(int customerId, int voucherCategoryId, int discount) {

        // nếu customer ko tồn tại trong voucher sẽ báo lỗi
        // voucher sẽ 2 loại update hay new mới
        try {
            Optional<Customer> customer = customerRepository.findById(customerId);
            Optional<Voucher> voucher = voucherRepository.findByCustomerCustomerId(customerId);

            if (!customer.isPresent() && !voucher.isPresent()) {
                throw new CustomException("Your id  " + customerId + "customer does not exist .", HttpStatus.NOT_FOUND);
            }
            String getInfoVoucher = setVoucherByCategory(customer, voucher, voucherCategoryId,discount);

            return new CustomResponse(getInfoVoucher, HttpStatus.OK.value(), "voucher");

        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), new Voucher());
        }
    }


    private String setVoucherByCategory(Optional<Customer> customer, Optional<Voucher> voucher, int voucherCategoryId, int discount) {
        String mess = "";

        Voucher newVoucher = voucher.orElseGet(() -> Voucher.builder()
                .customer(customer.get())
                .discount(discount)
                .status(true)
                .startTime(new Date())
                .endTime(DateUtils.getEndDate())
                .build());

        switch (voucherCategoryId) {
            case 1: {
                newVoucher.setDiscount(discount);
                mess = "You have received a 5% off Shipping coupon!";
                break;
            }
            case 2: {
                newVoucher.setDiscount(discount);
                mess = "You have received a 3% off Discount coupon!";
                break;
            }
            default: {
                mess = "*data null in voucher category!";
                break;
            }
        }

        voucherRepository.save(newVoucher);
        return mess;
    }
}
