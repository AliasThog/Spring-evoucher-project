package com.example.evoucherproject.service.Impl;

import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.model.entity.Purchase;
import com.example.evoucherproject.model.entity.Voucher;
import com.example.evoucherproject.repository.CustomerRepository;
import com.example.evoucherproject.repository.ProductRepository;
import com.example.evoucherproject.repository.PurchaseRepository;
import com.example.evoucherproject.repository.VoucherRepository;
import com.example.evoucherproject.service.PurchaseService;
import com.example.evoucherproject.service.VoucherService;
import com.example.evoucherproject.ultil.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherService voucherService;

    @Override
    public List<String> paymentCustomer(int customerId, int productId) {
        deleteExpiredVouchersByCustomerId(customerId);
        addOrUpdateCustomerBuyProduct(customerId, productId);
        Voucher voucher = insertOrUpdateVoucher(customerId, productId);
        return getInformationCustomerBuyProduct(customerId, productId, voucher);
    }

    private List<String> getInformationCustomerBuyProduct(int customerId, int productId, Voucher voucher) {
        Purchase purchase = purchaseRepository.getByCustomerAndProduct(customerId, productId).get();
        List<String> ls = new ArrayList<>();
        Double money = purchaseRepository.TotalQuantityByCustomerIdAndProductId(customerId, productId);
        ls.add("Khách hàng id : " + purchase.getCustomer().getCustomerId() + " - " + purchase.getCustomer().getName() + " mua sản phẩm : " + purchase.getProduct().getName());
        ls.add("tổng giá tiền hiện tại : " + money.toString());
        Double amount = money - (money * (voucher.getDiscount()) / 100);
        ls.add("sau khi áp dụng mã voucher " + voucher.getDiscount() + "% : " + amount);
        ls.add("Số lượng sản phẩm : " + purchase.getQuantity());
        return ls;
    }

    /*    private void applyDiscountDuringLateNight(Voucher voucher, int customerId) {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalTime startTime = LocalTime.of(0, 0); // 12h tối
            LocalTime endTime = LocalTime.of(1, 0); // 1h sáng
            // Thực hiện giảm giá cho khách hàng từ 12h tối tới 1h sáng
            if (currentTime.toLocalTime().isAfter(startTime) && currentTime.toLocalTime().isBefore(endTime)) {
                voucher = insertOrUpdateVoucher(customerId);
                return;
            }
        }

        private void applyDiscountOnSunday(Voucher voucher, int customerId) {
            LocalDate currentDate = LocalDate.now();
            // Thực hiện giảm giá sunday hang` tuan`
            if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                // voucher is exits
                voucher = insertOrUpdateVoucher(customerId);
                return;
            }
        }

        private void applyDiscountOnSpecialOccasions(Voucher voucher, int customerId) {
            LocalDate currentDate = LocalDate.now();
            // Kiểm tra nếu ngày hiện tại là một trong các ngày lễ yêu cầu (giáng sinh, tết, ngày khai trương)
            if (DateUtils.isChristmas(currentDate) || DateUtils.isNewYear(currentDate) || DateUtils.isGrandOpening(currentDate)) {
                // voucher is exits
                voucher = insertOrUpdateVoucher(customerId);
                return;
            }
        }
    */
    private void addOrUpdateCustomerBuyProduct(int customerId, int productId) {
        // nếu lần đầu mua hàng
        if (!purchaseRepository.existsByCustomerIdAndProductId(customerId, productId)) {
            purchaseRepository.save(Purchase.builder()
                    .purchaseTime(DateUtils.currentDateTime())
                    .customer(customerRepository.findById(customerId).get())
                    .product(productRepository.findById(productId).get())
                    .quantity(1).build());
        } else {
            // cập nhật số lượng khi mua hàng
            Optional<Purchase> purchase = purchaseRepository.getByCustomerAndProduct(customerId, productId);
            if (!purchase.isPresent())
                throw new CustomException("id customer and product does not exits!", HttpStatus.NOT_FOUND);
            purchase.get().setQuantity(purchase.get().getQuantity() + 1);
            purchaseRepository.save(purchase.get());
        }

    }

    private Voucher insertOrUpdateVoucher(int customerId, int productId) {
        Optional<Voucher> voucherOptional = voucherRepository.findByCustomerCustomerId(customerId);
        // nếu voucher tồn tại  và trạng thái bằng true và  còn hàng sử dụng thì sẽ update voucher

        if (voucherOptional.isPresent()) {
            Voucher voucher = voucherOptional.get();
            if (voucher.isStatus() == true && DateUtils.isDateToday(voucher.getEndTime())) {
                voucher.setDiscount(7);
            }
            return voucherRepository.save(voucher);

        } else {
            if (purchaseRepository.isCustomerExceededPurchaseLimit(customerId, productId)) {
                // còn nếu chưa tồn tại mà khách hàng mua 5 lần thì nhận 1 voucher mới
                voucherService.saveVoucher(customerId, 7);
            }
        }
        return new Voucher();
    }

    private void deleteExpiredVouchersByCustomerId(int customerId) {
        Optional<Voucher> isVoucher = voucherRepository.findByCustomerCustomerId(customerId);
        if (isVoucher.isPresent()) {
            if (DateUtils.isDateToday(isVoucher.get().getEndTime())) {
                voucherRepository.delete(isVoucher.get());
            }
        }
    }
}
