package com.kttk.CostumeRental.service;

import com.kttk.CostumeRental.DAO.*;
import com.kttk.CostumeRental.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    @Autowired private CartDAO cartDAO;
    @Autowired private CustomerDAO customerDAO;
    @Autowired private CostumeDAO costumeDAO;
    @Autowired private CartItemDAO cartItemDAO;

    @Transactional
    public void addToCart(Long customerId, Long costumeId, int quantity) {
        Cart cart = cartDAO.findByCustomerId(customerId);
        if (cart == null) {
            cart = new Cart();
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) throw new RuntimeException("Khách hàng không tồn tại!");
            cart.setCustomer(customer);
        }

        Costume costume = costumeDAO.getById(costumeId);
        if (costume == null) throw new RuntimeException("Trang phục không tồn tại!");

        CartItem existingItem = null;

        for (CartItem item : cart.getItems()) {
            if (item.getCostume().getId().equals(costumeId)) {
                existingItem = item;
                break;
            }
        }

        int currentQtyInCart = (existingItem == null) ? 0 : existingItem.getQuantity();
        int totalQtyRequested = currentQtyInCart + quantity;

        if (totalQtyRequested > costume.getQuantityAvailable()) {
            throw new RuntimeException("Không đủ hàng! Tồn kho: " + costume.getQuantityAvailable()
                    + ", Bạn đã chọn: " + currentQtyInCart
                    + ", Muốn thêm: " + quantity);
        }

        if (existingItem != null) {
            existingItem.setQuantity(totalQtyRequested);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setCostume(costume);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        cartDAO.save(cart);
    }

    @Transactional
    public void removeItem(Long cartItemId) {
        cartItemDAO.deleteById(cartItemId);
    }
}