package com.kttk.CostumeRental.service;

import com.kttk.CostumeRental.DAO.*;
import com.kttk.CostumeRental.DTO.BookingRequestDTO;
import com.kttk.CostumeRental.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {
    @Autowired private CustomerDAO customerDAO;
    @Autowired private CartDAO cartDAO;
    @Autowired private BookingDAO bookingDAO;

    @Transactional
    public Booking confirmBooking(BookingRequestDTO req) {
        // 1. Tìm khách hàng (hoặc tạo mới nếu chưa có)
        // Ở đây giả định khách đã đăng nhập và có ID
        Customer customer = customerDAO.findById(req.getCustomerId());

        // 2. Lấy giỏ hàng
        Cart cart = cartDAO.findByCustomerId(req.getCustomerId());
        if(cart == null || cart.getItems().isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        // 3. Tạo Booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setRentalDate(req.getRentalDate());
        booking.setReturnDateExpected(req.getReturnDate());
        booking.setStatus("Confirmed");

        // 4. Tạo Bill & Payment (Cọc 30%)
        double total = cart.getItems().stream().mapToDouble(i -> i.getCostume().getRentalPrice() * i.getQuantity()).sum();
        double deposit = total * 0.3;

        Bill bill = new Bill();
        bill.setBooking(booking);
        bill.setTotalAmount(total);
        bill.setDepositAmount(deposit);
        bill.setStatus("PartiallyPaid");

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(deposit);
        payment.setPaymentDate(new Date());
        payment.setNote("Deposit");

        bill.setPayments(new ArrayList<>(List.of(payment)));
        booking.setBill(bill);

        // 5. Lưu tất cả (Cascade sẽ lưu Bill và Payment theo Booking)
        bookingDAO.save(booking);

        // 6. Xóa giỏ hàng sau khi đặt
        cart.getItems().clear();
        cartDAO.save(cart);

        return booking;
    }
}