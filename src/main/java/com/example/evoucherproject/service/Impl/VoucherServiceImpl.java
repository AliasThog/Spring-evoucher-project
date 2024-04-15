package com.example.evoucherproject.service.Impl;

import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.entity.Customer;
import com.example.evoucherproject.model.entity.Voucher;
import com.example.evoucherproject.repository.CustomerRepository;
import com.example.evoucherproject.repository.VoucherRepository;
import com.example.evoucherproject.service.AccountService;
import com.example.evoucherproject.service.CustomerService;
import com.example.evoucherproject.service.VoucherService;
import com.example.evoucherproject.ultil.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;


@Service
public class VoucherServiceImpl implements VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomResponse saveVoucher(int customerId, int discount) {

        // nếu customer ko tồn tại trong voucher sẽ báo lỗi
        // voucher sẽ 2 loại update hay new mới
        try {
            Optional<Customer> customer = customerRepository.findById(customerId);
            Optional<Voucher> voucher = voucherRepository.findByCustomerCustomerId(customerId);

            if (!customer.isPresent() && !voucher.isPresent()) {
                throw new CustomException("Your id  " + customerId + "customer does not exist .", HttpStatus.NOT_FOUND);
            }
            voucher.ifPresentOrElse(
                    v -> {
                        v.setDiscount(discount);
                        voucherRepository.save(v);
                    },
                    () -> {
                        Voucher newVoucher = Voucher.builder()
                                .customer(customer.get())
                                .discount(discount)
                                .status(true)
                                .startTime(new Date())
                                .endTime(DateUtils.getEndDate())
                                .build();
                        voucherRepository.save(newVoucher);
                    }
            );
            return new CustomResponse("You have received a " + discount + "% discount coupon !", HttpStatus.OK.value(), "voucher");

        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), new Voucher());
        }
    }
    //  thêm kh sẽ được theo voucher với đk như nào, đảm bảo kh tồn tại , thứ 2 nếu kh chưa có voucher sẽ thêm voucher , còn có rồi ko canan thêm vào b
}
