package com.example.evoucherproject.controller;

import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.dto.request.account.CreateAccountByAdminDto;
import com.example.evoucherproject.model.dto.request.account.LoginUserDto;
import com.example.evoucherproject.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserDetailsService detailsService;
    @Autowired
    private AccountService accountService;

    @PostMapping("/login-jwt")
    public CustomResponse authenticateUser(@RequestBody @Valid LoginUserDto dto , BindingResult result) {
        return accountService.validateUserAndGenerateToken(dto,result,detailsService);
    }
    @GetMapping("/admin")
    public CustomResponse admin() {
        return new CustomResponse("Welcome come admin", HttpStatus.OK, "");
    }
    @GetMapping("/user")
    public CustomResponse user() {
        return new CustomResponse("Welcome come USER", HttpStatus.OK, "");
    }

    @GetMapping("/home")
    public CustomResponse home(Authentication authentication ) {
        return accountService.getUserInfoAfterAuthentication(authentication);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("/login");
    }

    @PutMapping("/admin/update/{id}")
    public CustomResponse updateAccountByAdmin(@PathVariable Long id,@RequestBody @Valid CreateAccountByAdminDto dto,BindingResult result) {
        return accountService.updateAccountByAdmin(id,dto, result);
    }
    @DeleteMapping("/admin/delete/{id}")
    public CustomResponse deleteAccountByAdmin(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }
}
