package com.example.evoucherproject.model.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "voucher_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voucherCateId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}