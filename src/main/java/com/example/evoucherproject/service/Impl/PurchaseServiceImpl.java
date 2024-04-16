package com.example.evoucherproject.service.Impl;

import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.entity.Customer;
import com.example.evoucherproject.model.entity.Product;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public CustomResponse paymentCustomer(int customerId, int productId) {
        try {
            deleteExpiredVouchersByCustomerId(customerId);
            Purchase purchase = addOrUpdateCustomerBuyProduct(customerId, productId);
            Voucher voucher = insertOrUpdateVoucher(customerId, productId);
            return new CustomResponse("Payment Successfully!!!", HttpStatus.OK.value(), getInformationCustomerBuyProduct(customerId, productId, voucher));
        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), "");
        }

    }

    private List<String> getInformationCustomerBuyProduct(int customerId, int productId, Voucher voucher) {
        Purchase purchase = purchaseRepository.getByCustomerAndProduct(customerId, productId).get();
        List<String> ls = new ArrayList<>();
        Double money = purchaseRepository.TotalQuantityByCustomerIdAndProductId(customerId, productId);
        ls.add("Customer id: " + purchase.getCustomer().getCustomerId() + " - " + purchase.getCustomer().getName() + " buy product: " + purchase.getProduct().getName());
        ls.add("Total current: " + money.toString());
        Double amount = money - (money * (voucher.getDiscount()) / 100);
        ls.add("After payment, applying the voucher code: "+ voucher.getVoucherCategory().getName() + " " + voucher.getDiscount() + "% : " + amount);
        ls.add("Quantity product: " + purchase.getQuantity());
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
    private Purchase addOrUpdateCustomerBuyProduct(int customerId, int productId) {
        Optional<Purchase> purchase = purchaseRepository.getByCustomerAndProduct(customerId, productId);
        Optional<Customer> customer = customerRepository.findById(customerId);
        Optional<Product> product = productRepository.findById(productId);
        if (!customer.isPresent() && !product.isPresent() && !purchase.isPresent())
            throw new CustomException("id customer and product does not exits!", HttpStatus.NOT_FOUND);
        // nếu lần đầu mua hàng
        if (!purchaseRepository.existsByCustomerIdAndProductId(customerId, productId)) {
            return purchaseRepository.save(Purchase.builder()
                    .purchaseTime(DateUtils.currentDateTime())
                    .customer(customer.get())
                    .product(product.get())
                    .quantity(1).build());
        } else {
            // cập nhật số lượng khi mua hàng
            purchase.get().setQuantity(purchase.get().getQuantity() + 1);
            return purchaseRepository.save(purchase.get());
        }

    }
    // này chỉ áp dung voucher khi mua hàng quá 5 lần
    private Voucher insertOrUpdateVoucher(int customerId, int productId) {
        Optional<Voucher> voucherOptional = voucherRepository.findByCustomerCustomerId(customerId);
        // nếu voucher tồn tại
        if (voucherOptional.isPresent()) {
            Voucher voucher = voucherOptional.get();
            // nếu tồn tại voucher discount và nếu mua quá 5 lần, ko áp dụng voucher shipper
            if (voucher.isStatus() == true && voucher.getVoucherCategory().getVoucherCateId() == 2  &&  purchaseRepository.isCustomerExceededPurchaseLimit(customerId, productId)) {
                voucher.setDiscount(7);
                voucher.setStartTime(new Date());
                voucher.setEndTime(DateUtils.getEndDate());
            }
            return voucherRepository.save(voucher);

        } else { // nếu voucher chưa tồn tại
            if (purchaseRepository.isCustomerExceededPurchaseLimit(customerId, productId)) {
                // còn nếu chưa tồn tại mà khách hàng mua 5 lần thì nhận 1 voucher discount mới
                voucherService.saveVoucher(customerId, 2,7);
            }
        }
        return new Voucher();
    }

    private void deleteExpiredVouchersByCustomerId(int customerId) {
        Optional<Voucher> isVoucher = voucherRepository.findByCustomerCustomerId(customerId);
        if (isVoucher.isPresent() && DateUtils.isDateToday(isVoucher.get().getEndTime())) {
            voucherRepository.delete(isVoucher.get());
        }
    }
}
