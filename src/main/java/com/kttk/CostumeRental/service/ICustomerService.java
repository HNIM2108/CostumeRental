package com.kttk.CostumeRental.service;


import com.kttk.CostumeRental.entity.Customer;
import java.util.List;

public interface ICustomerService {
    List<Customer> getAllCustomers();
    List<Customer> searchCustomers(String keyword);
    Customer getCustomerById(Long id);
    Customer saveCustomer(Customer customer);
    void deleteCustomer(Long id);
}