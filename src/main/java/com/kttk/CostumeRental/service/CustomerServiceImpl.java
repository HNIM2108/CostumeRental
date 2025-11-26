package com.kttk.CostumeRental.service;

import com.kttk.CostumeRental.DAO.CustomerDAO;
import com.kttk.CostumeRental.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("realCustomerService")
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerDAO customerDAO; // Inject trực tiếp Class DAO

    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    @Override
    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.isEmpty()) return customerDAO.getAllCustomers();
        return customerDAO.searchCustomer(keyword);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerDAO.findById(id);
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        if (customer.getId() == null) {
            if (customer.getUsername() == null || customer.getUsername().isEmpty()) {
                customer.setUsername(customer.getPhone());
            }
            if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                customer.setPassword("123456");
            }
            customer.setRole("CUSTOMER");
            customer.setStatus(true);

            return customerDAO.addCustomer(customer);
        } else {
            Customer existingCustomer = customerDAO.findById(customer.getId());

            if (existingCustomer != null) {
                existingCustomer.setFullName(customer.getFullName());
                existingCustomer.setPhone(customer.getPhone());
                existingCustomer.setEmail(customer.getEmail());
                existingCustomer.setAddress(customer.getAddress());
                existingCustomer.setLoyaltyPoints(customer.getLoyaltyPoints());

                return customerDAO.updateCustomer(existingCustomer);
            } else {
                throw new RuntimeException("Khách hàng không tồn tại để cập nhật!");
            }
        }
    }

    @Override
    public void deleteCustomer(Long id) {
        customerDAO.deleteCustomer(id);
    }
}