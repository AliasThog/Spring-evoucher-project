package com.example.evoucherproject.service.Impl;

import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.mapper.DataMapper;
import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.dto.request.customer.CreateCustomerDto;
import com.example.evoucherproject.model.entity.Customer;
import com.example.evoucherproject.repository.CustomerRepository;
import com.example.evoucherproject.service.CustomerService;
import com.example.evoucherproject.ultil.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service// 1 bean, để khi chay project nó sẽ tiêm phụ thuộc vào , hay dữ lữ vào
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomResponse getByIdCustomer(Integer id) {
        Optional<Customer> exitsCustomer = customerRepository.findById(id);
        if (!exitsCustomer.isPresent()) {
            throw new CustomException("Khach hang ko tim thay!", HttpStatus.NOT_FOUND);
        }
        return new CustomResponse("Create customer with your id  " + id + "successfully!",
                HttpStatus.OK, exitsCustomer);
    }


    @Override
    public CustomResponse createEmployee(CreateCustomerDto dto, BindingResult result) {
        if (customerRepository.existsByPhone(dto.getPhone())) {
            throw new CustomException("Phone existing!", HttpStatus.CONFLICT);
        }
        if (result.hasErrors()) {
            throw new CustomException(ValidationUtils.getValidationErrorString(result), HttpStatus.BAD_REQUEST);
        }
        Customer customer = DataMapper.toEntity(dto, Customer.class);
        return new CustomResponse("Customer created successfully!",
                HttpStatus.CREATED, customerRepository.save(customer));
    }
}
