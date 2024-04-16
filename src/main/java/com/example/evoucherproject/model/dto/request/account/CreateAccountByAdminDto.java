package com.example.evoucherproject.model.dto.request.account;

import com.example.evoucherproject.model.entity.Role;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateAccountByAdminDto { // have data

    private boolean status;
    private Set<Integer> roleId ;
    //ko dc la

}