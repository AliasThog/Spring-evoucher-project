package com.example.evoucherproject.repository;

import com.example.evoucherproject.model.entity.Customer;
import com.example.evoucherproject.model.entity.Purchase;
import com.example.evoucherproject.model.entity.Voucher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,Integer> {
    // tim kiem id
    //check status voucher
    @Query("SELECT CASE WHEN  v.status = true THEN true ELSE false END  FROM Voucher v where v.customer.customerId = :customerId")
    Boolean checkCustomerAndStatus( @Param("customerId") int customerId);


    // check customer of voucher is exits
    boolean existsByCustomerCustomerId(int customerId);

    // trả về dữ liệu  voucher theo id customer
    Optional<Voucher> findByCustomerCustomerId(int customerId);


}
