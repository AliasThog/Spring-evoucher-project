package com.example.evoucherproject.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@Entity
@Table (name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"customer"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;
    @Column(unique = true)
    private String name;
    @Temporal(TemporalType.DATE)
    private Date birthday;
    @Column(unique = true,length = 50)
    private String email;
    @Column(unique = true,length = 50)
    private String address;
    @Column(unique = true,length = 50)
    private String phone;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;


}
