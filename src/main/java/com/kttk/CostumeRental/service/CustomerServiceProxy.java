package com.kttk.CostumeRental.service;

import com.kttk.CostumeRental.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Primary
public class CustomerServiceProxy implements ICustomerService {

    @Autowired
    @Qualifier("realCustomerService")
    private ICustomerService realService;


    private boolean checkAccess() {
        System.out.println("[PROXY] Checking access permissions...");
        return true; // Cho phép đi qua
    }

    @Override
    public List<Customer> getAllCustomers() {
        System.out.println("[PROXY] Logging: Getting all customers");
        return realService.getAllCustomers();
    }

    @Override
    public List<Customer> searchCustomers(String keyword) {
        return realService.searchCustomers(keyword);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return realService.getCustomerById(id);
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        if (checkAccess()) {
            if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
                throw new RuntimeException("Phone number is required!");
            }
            return realService.saveCustomer(customer);
        }
        throw new RuntimeException("Access Denied");
    }

    @Override
    public void deleteCustomer(Long id) {
        if (checkAccess()) {
            System.out.println("[PROXY] Logging: Deleting customer " + id);
            realService.deleteCustomer(id);
        }
    }
}