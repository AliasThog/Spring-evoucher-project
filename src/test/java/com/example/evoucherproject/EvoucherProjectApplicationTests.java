package com.example.evoucherproject;

import com.example.evoucherproject.model.entity.Voucher;
import com.example.evoucherproject.repository.VoucherRepository;
import com.example.evoucherproject.ultil.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Optional;

@SpringBootTest
class EvoucherProjectApplicationTests {

    @Autowired
    private VoucherRepository voucherRepository;
    @Test
    void contextLoads() {
        Optional<Voucher> voucher = voucherRepository.findByCustomerCustomerId(1);
        System.out.println(voucher.get().getEndTime().getClass().getName());
        System.out.println(new Date().getClass().getName());
        if (voucher.isPresent()){
            if (DateUtils.isDateToday(voucher.get().getEndTime())){
                System.out.println("Match");
                voucherRepository.delete(voucher.get());
            }
        }
    }

}
