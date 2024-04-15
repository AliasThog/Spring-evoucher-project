package com.example.evoucherproject.model.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "voucher_category")
public class VoucherCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}