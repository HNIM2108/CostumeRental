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
        // Gọi hàm của Class DAO
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
        if(customer.getRole() == null) customer.setRole("CUSTOMER");

        // Tách logic Add và Update rõ ràng nếu muốn, hoặc dùng chung logic kiểm tra ID
        if (customer.getId() == null) {
            return customerDAO.addCustomer(customer);
        } else {
            return customerDAO.updateCustomer(customer);
        }
    }

    @Override
    public void deleteCustomer(Long id) {
        customerDAO.deleteCustomer(id);
    }
}