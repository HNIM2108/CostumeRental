package com.kttk.CostumeRental.DAO;


import com.kttk.CostumeRental.entity.Customer;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class CustomerDAO extends DAO {

    public List<Customer> getAllCustomers() {
        String jpql = "SELECT c FROM Customer c";
        TypedQuery<Customer> query = entityManager.createQuery(jpql, Customer.class);
        return query.getResultList();
    }

    public Customer findById(Long id) {
        return entityManager.find(Customer.class, id);
    }

    public List<Customer> searchCustomer(String keyword) {
        String jpql = "SELECT c FROM Customer c WHERE c.fullName LIKE :keyword OR c.phone LIKE :keyword";
        TypedQuery<Customer> query = entityManager.createQuery(jpql, Customer.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    public Customer addCustomer(Customer customer) {
        entityManager.persist(customer);
        return customer;
    }

    public Customer updateCustomer(Customer customer) {
        return entityManager.merge(customer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = findById(id);
        if (customer != null) {
            entityManager.remove(customer);
        }
    }
}