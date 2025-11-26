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

    @Transactional
    public void addToCart(Long customerId, Long costumeId, int quantity) {
        // 1. Lấy hoặc Tạo giỏ hàng
        Cart cart = cartDAO.findByCustomerId(customerId);
        if (cart == null) {
            cart = new Cart();
            // Lưu ý: Dùng hàm findById theo đúng DAO của bạn
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) throw new RuntimeException("Khách hàng không tồn tại!");
            cart.setCustomer(customer);
        }

        // 2. Lấy thông tin trang phục
        Costume costume = costumeDAO.getById(costumeId);
        if (costume == null) throw new RuntimeException("Trang phục không tồn tại!");

        // 3. Kiểm tra xem món này đã có trong giỏ chưa
        CartItem existingItem = null;

        // Duyệt qua danh sách item đang có trong giỏ
        for (CartItem item : cart.getItems()) {
            if (item.getCostume().getId().equals(costumeId)) {
                existingItem = item;
                break;
            }
        }

        // 4. Tính tổng số lượng dự kiến (Đang có + Muốn thêm)
        int currentQtyInCart = (existingItem == null) ? 0 : existingItem.getQuantity();
        int totalQtyRequested = currentQtyInCart + quantity;

        // 5. CHECK TỒN KHO (Xử lý vấn đề 2)
        if (totalQtyRequested > costume.getQuantityAvailable()) {
            throw new RuntimeException("Không đủ hàng! Tồn kho: " + costume.getQuantityAvailable()
                    + ", Bạn đã chọn: " + currentQtyInCart
                    + ", Muốn thêm: " + quantity);
        }

        // 6. Cập nhật hoặc Thêm mới (Xử lý vấn đề 1)
        if (existingItem != null) {
            // Nếu đã có -> Cập nhật số lượng
            existingItem.setQuantity(totalQtyRequested);
        } else {
            // Nếu chưa có -> Tạo dòng mới
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setCostume(costume);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        // 7. Lưu giỏ hàng
        cartDAO.save(cart);
    }

    @Transactional
    public void removeFromCart(Long cartItemId) {
        // Gọi hàm xóa của CartDAO
        cartDAO.removeCartItem(cartItemId);
    }
}