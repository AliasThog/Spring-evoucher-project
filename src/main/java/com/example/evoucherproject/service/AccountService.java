package com.example.evoucherproject.service;

import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.dto.request.account.CreateAccountByAdminDto;
import com.example.evoucherproject.model.dto.request.account.CreateAccountByUserDto;
import com.example.evoucherproject.model.dto.request.account.LoginUserDto;
import com.example.evoucherproject.model.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();

    CustomResponse getAccountById(Long id) ;

    CustomResponse createAccountByUser(CreateAccountByUserDto dto, BindingResult result);

    CustomResponse updateAccountByAdmin(Long id,CreateAccountByAdminDto dto, BindingResult result);

    CustomResponse deleteAccount(Long id);

    CustomResponse validateUserAndGenerateToken(LoginUserDto dto, BindingResult result, UserDetailsService detailsService);

    CustomResponse getUserInfoAfterAuthentication(Authentication authentication);


}
