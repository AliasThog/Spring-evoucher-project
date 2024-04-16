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
    public CustomResponse saveVoucher(int customerId, int voucherCategoryId) {

        // nếu customer ko tồn tại trong voucher sẽ báo lỗi
        // voucher sẽ 2 loại update hay new mới
        try {
            Optional<Customer> customer = customerRepository.findById(customerId);
            Optional<Voucher> voucher = voucherRepository.findByCustomerCustomerId(customerId);

            if (!customer.isPresent() && !voucher.isPresent()) {
                throw new CustomException("Your id  " + customerId + "customer does not exist .", HttpStatus.NOT_FOUND);
            }
            String getInfoVoucher = setVoucherByCategory(customer, voucher, voucherCategoryId);

            return new CustomResponse(getInfoVoucher, HttpStatus.OK.value(), "voucher");

        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), new Voucher());
        }
    }


    private String setVoucherByCategory(Optional<Customer> customer, Optional<Voucher> voucher, int voucherCategoryId) {
        String mess = "";
        switch (voucherCategoryId) {
            case 1: {
                voucher.ifPresentOrElse(
                        v -> {
                            v.setDiscount(5);
                            voucherRepository.save(v);
                        },
                        () -> {
                            Voucher newVoucher = Voucher.builder()
                                    .customer(customer.get())
                                    .discount(voucherCategoryId)
                                    .status(true)
                                    .startTime(new Date())
                                    .endTime(DateUtils.getEndDate())
                                    .build();
                            voucherRepository.save(newVoucher);
                        }
                );
                mess = "You have received a 5% off Shipping coupon!";
                break;
            }
            case 2: {
                voucher.ifPresentOrElse(
                        v -> {
                            v.setDiscount(3);
                            voucherRepository.save(v);
                        },
                        () -> {
                            Voucher newVoucher = Voucher.builder()
                                    .customer(customer.get())
                                    .discount(3)
                                    .status(true)
                                    .startTime(new Date())
                                    .endTime(DateUtils.getEndDate())
                                    .build();
                            voucherRepository.save(newVoucher);
                        }
                );
                mess = "You have received a 3% off Discount coupon!";
                break;
            }
            default: {
                mess = "*data null in voucher category!";
                break;
            }
        }
        return mess;
    }
    //  thêm kh sẽ được theo voucher với đk như nào, đảm bảo kh tồn tại , thứ 2 nếu kh chưa có voucher sẽ thêm voucher , còn có rồi ko canan thêm vào b
}
