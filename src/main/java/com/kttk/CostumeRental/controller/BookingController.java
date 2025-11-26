package com.kttk.CostumeRental.controller;

import com.kttk.CostumeRental.DAO.CostumeDAO;
import com.kttk.CostumeRental.DAO.PromotionDAO;
import com.kttk.CostumeRental.DTO.BookingRequestDTO;
import com.kttk.CostumeRental.entity.Cart;
import com.kttk.CostumeRental.DAO.CartDAO;
import com.kttk.CostumeRental.entity.Costume;
import com.kttk.CostumeRental.pattern.command.*;
import com.kttk.CostumeRental.service.BookingService;
import com.kttk.CostumeRental.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    @Autowired private CostumeDAO costumeDAO; // Gọi thẳng DAO cho luồng Read
    @Autowired private CartService cartService;
    @Autowired private BookingService bookingService;

    @Autowired private CartDAO cartDAO;

    @Autowired private PromotionDAO promotionDAO;

    @GetMapping("/costumes")
    public ResponseEntity<List<Costume>> getAllCostumes(@RequestParam(required = false) String search) {
        if(search != null) return ResponseEntity.ok(costumeDAO.search(search));
        return ResponseEntity.ok(costumeDAO.getAll());
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart(@RequestParam Long customerId) {
        Cart cart = cartDAO.findByCustomerId(customerId);
        if (cart == null) {
            return ResponseEntity.ok().body(List.of()); // Trả về list rỗng nếu chưa có giỏ
        }
        return ResponseEntity.ok(cart.getItems()); // Trả về danh sách CartItem
    }
    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestParam Long customerId, @RequestParam Long costumeId, @RequestParam int quantity) {

        try {
            // Gọi Service (có thể bọc trong Command nếu muốn đúng pattern)
            ICommand command = new AddToCartCommand(cartService, customerId, costumeId, quantity);
            return ResponseEntity.ok(command.execute());
        } catch (Exception e) {
            // --- SỬA LỖI: Trả về message lỗi đơn giản ---
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/cart/item/{id}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long id) {
        try {
            cartService.removeFromCart(id);
            return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xóa: " + e.getMessage());
        }
    }
    @PostMapping("/booking/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody BookingRequestDTO request) {
        try {
            ICommand command = new ConfirmBookingCommand(bookingService, request);
            return ResponseEntity.ok(command.execute());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/promotions/check")
    public ResponseEntity<?> checkPromotion(
            @RequestParam String code,
            @RequestParam double totalAmount,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date rentalDate) { // Thêm tham số này
        try {
            var promotion = promotionDAO.findByCode(code);

            if (promotion == null) {
                return ResponseEntity.badRequest().body("Mã không tồn tại!");
            }

            // 1. KIỂM TRA THỜI GIAN (Dựa trên ngày thuê khách chọn)
            if (rentalDate.before(promotion.getStartDate()) || rentalDate.after(promotion.getEndDate())) {
                return ResponseEntity.badRequest().body("Mã này không áp dụng cho ngày thuê bạn chọn!");
            }

            // 2. KIỂM TRA GIÁ TRỊ ĐƠN HÀNG
            if (totalAmount < promotion.getMinOrderValue()) {
                long minVal = (long) promotion.getMinOrderValue();
                return ResponseEntity.badRequest().body("Đơn hàng phải từ " + minVal + " đ mới được áp dụng!");
            }

            return ResponseEntity.ok(promotion.getDiscountPercent());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }
}