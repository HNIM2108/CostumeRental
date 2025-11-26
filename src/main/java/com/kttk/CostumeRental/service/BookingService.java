package com.kttk.CostumeRental.service;

import com.kttk.CostumeRental.DAO.*;
import com.kttk.CostumeRental.DTO.BookingRequestDTO;
import com.kttk.CostumeRental.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {
    @Autowired private CustomerDAO customerDAO;
    @Autowired private CartDAO cartDAO;
    @Autowired private BookingDAO bookingDAO;

    // --- BỔ SUNG CÁC DAO THEO THIẾT KẾ ---
    @Autowired private BookingDetailDAO bookingDetailDAO;
    @Autowired private BillDAO billDAO;
    @Autowired private PaymentDAO paymentDAO;
    @Autowired private CostumeDAO costumeDAO;
    @Autowired private PromotionDAO promotionDAO;

    @Transactional
    public Booking confirmBooking(BookingRequestDTO req) {
        // 1. Tìm khách hàng
        Customer customer = customerDAO.findById(req.getCustomerId());
        if (customer == null) throw new RuntimeException("Khách hàng không tồn tại!");

        // 2. Lấy giỏ hàng
        Cart cart = cartDAO.findByCustomerId(req.getCustomerId());
        if(cart == null || cart.getItems().isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        // 3. Tạo Booking (Chưa có Detail, Bill)
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setRentalDate(req.getRentalDate());
        booking.setReturnDateExpected(req.getReturnDate());
        booking.setStatus("Confirmed");

        // Tính tổng tiền gốc
        double totalItemPrice = cart.getItems().stream()
                .mapToDouble(i -> i.getCostume().getRentalPrice() * i.getQuantity())
                .sum();
        booking.setTotalAmount(totalItemPrice);

        // 4. Lưu Booking trước để lấy ID (Bước quan trọng trong Sequence)
        bookingDAO.save(booking);

        // 5. VÒNG LẶP: Lưu từng BookingDetail (Tuân thủ thiết kế)
        for (CartItem item : cart.getItems()) {
            BookingDetail detail = new BookingDetail();
            detail.setBooking(booking);
            detail.setCostume(item.getCostume());
            detail.setQuantity(item.getQuantity());
            detail.setPriceAtBooking(item.getCostume().getRentalPrice());

            // Gọi BookingDetailDAO
            bookingDetailDAO.save(detail);

            // Trừ tồn kho (Logic thực tế)
            Costume costume = item.getCostume();
            costume.setQuantityAvailable(costume.getQuantityAvailable() - item.getQuantity());
            costumeDAO.updateCostume(costume); // Hàm này giả định đã có trong CostumeDAO
        }

        // 6. Tạo & Lưu Bill (Tách biệt)
        // (Giả sử logic giảm giá đã tính và gửi kèm hoặc tính lại ở đây, tạm thời để 0)
        double discount = 0;
        double finalPrice = totalItemPrice - discount;
        double deposit = finalPrice * 0.3;

        Bill bill = new Bill();
        bill.setBooking(booking);
        bill.setCreatedDate(new Date());
        bill.setTotalItemAmount(totalItemPrice);
        bill.setDiscountAmount(discount);
        bill.setFinalAmount(finalPrice);
        bill.setDepositAmount(deposit);
        bill.setStatus("PartiallyPaid");

        // Gọi BillDAO
        billDAO.createBill(bill);

        // 7. Tạo & Lưu Payment (Tách biệt)
        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(deposit);
        payment.setPaymentDate(new Date());
        payment.setNote("Deposit (Cọc)");

        // Gọi PaymentDAO
        paymentDAO.createPayment(payment);

        // 8. Xóa giỏ hàng
        cart.getItems().clear();
        cartDAO.save(cart);

        return booking;
    }
}