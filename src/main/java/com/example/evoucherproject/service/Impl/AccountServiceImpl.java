package com.example.evoucherproject.service.Impl;


import com.example.evoucherproject.auth.jwt.JwtTokenProvider;
import com.example.evoucherproject.auth.userdetails.CustomUserDetails;
import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.mapper.DataMapper;
import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.dto.request.account.CreateAccountByAdminDto;
import com.example.evoucherproject.model.dto.request.account.CreateAccountByUserDto;
import com.example.evoucherproject.model.dto.request.account.LoginUserDto;
import com.example.evoucherproject.model.dto.request.customer.CreateCustomerDto;
import com.example.evoucherproject.model.entity.Account;
import com.example.evoucherproject.model.entity.Customer;
import com.example.evoucherproject.repository.AccountRepository;
import com.example.evoucherproject.service.AccountService;
import com.example.evoucherproject.service.RoleService;
import com.example.evoucherproject.ultil.AuthenticationUtils;
import com.example.evoucherproject.ultil.DateUtils;
import com.example.evoucherproject.ultil.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleService roleService;

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public CustomResponse getAccountById(Long id) {
        try {
            Optional<Account> exitsAccount = accountRepository.findById(id);
            if (!exitsAccount.isPresent()) {
                throw new CustomException("Account ko tim thay!", HttpStatus.NOT_FOUND);
            }

            return new CustomResponse("Account with id: " + id + "successfully!",
                    HttpStatus.OK.value(), exitsAccount);
        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), "");
        }
    }

    @Override
    public CustomResponse createAccountByUser(CreateAccountByUserDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                throw new CustomException(ValidationUtils.getValidationErrorString(result), HttpStatus.BAD_REQUEST);
            }
            Account account = DataMapper.toEntity(dto, Account.class);
            account.setDate(DateUtils.currentDateTime());
            account.setPassword(passwordEncoder.encode(dto.getPassword()));
            account.setStatus(true);
            account.setRoles(roleService.getRolesByRoleIds(Set.of(2)));
            account.setCustomer(Customer.builder().name(account.getUsername()).account(account).build());
            return new CustomResponse("Account created by User successfully!",
                    HttpStatus.CREATED.value(), accountRepository.save(account));
        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), new CreateCustomerDto());
        }
    }

    @Override
    public CustomResponse updateAccountByAdmin(Long id,CreateAccountByAdminDto dto, BindingResult result) {
        try {
            Optional<Account> account = accountRepository.findById(id);
            if (!account.isPresent()) {
                throw new CustomException("Account not found", HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                throw new CustomException(ValidationUtils.getValidationErrorString(result), HttpStatus.BAD_REQUEST);
            }

            account.get().setStatus(dto.isStatus());
            account.get().setRoles(roleService.getRolesByRoleIds(dto.getRoleId()));

            return new CustomResponse("Account update by Admin successfully!",
                    HttpStatus.CREATED.value(),  accountRepository.save(account.get()));
        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), new CreateCustomerDto());
        }
    }


    @Override
    public CustomResponse deleteAccount(Long id) {
        try {
            Optional<Account> existingAccount = accountRepository.findById(id);
            if (!existingAccount.isPresent()) {
                throw new CustomException("Account not found: " + id, HttpStatus.BAD_REQUEST);
            }
            accountRepository.deleteById(id);
            return new CustomResponse("Account deleted successfully!", HttpStatus.ACCEPTED.value(), "");

        } catch (CustomException e) {

            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), null);
        }
    }

    @Override
    public CustomResponse validateUserAndGenerateToken(LoginUserDto dto, BindingResult result, UserDetailsService detailsService) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) detailsService.loadUserByUsername(dto.getUsername());

            if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) {
                throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
            }
            if (result.hasErrors()) {
                throw new CustomException(ValidationUtils.getValidationErrorString(result), HttpStatus.BAD_REQUEST);
            }
            // create JWT
            String token = jwtTokenProvider.generateToken(userDetails);
            return new CustomResponse("User Login successfully!", HttpStatus.CREATED.value(), token);

        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), "");
        }
    }

    @Override
    public CustomResponse getUserInfoAfterAuthentication(Authentication authentication) {
        try {
            if (authentication == null && !authentication.isAuthenticated()) {
                throw new CustomException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
            }
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String username = authentication.getName(); // Lấy tên người dùng từ xác thực
            List<String> roles = AuthenticationUtils.getRoles(customUserDetails);
            return new CustomResponse("*Welcome " + username + " to page home !!", HttpStatus.OK.value(), "username : " + username + " - " + roles);
        } catch (CustomException e) {
            return new CustomResponse(e.getMessage(), e.getHttpStatus().value(), "");
        }

    }
}