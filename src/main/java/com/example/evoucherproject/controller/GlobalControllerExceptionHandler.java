package com.example.evoucherproject.controller;

import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.dto.request.customer.CreateCustomerDto;
import com.example.evoucherproject.service.AccountService;
import com.example.evoucherproject.service.Impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<CustomResponse> handleCustomException(CustomException e){
        CustomResponse response = new CustomResponse();
        response.setMess(e.getMessage());
        response.setHttpStatus(e.getHttpStatus());
        response.setData(new CreateCustomerDto());
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }
}