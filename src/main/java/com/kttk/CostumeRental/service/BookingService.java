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
    @Autowired private BookingDetailDAO bookingDetailDAO;
    @Autowired private BillDAO billDAO;
    @Autowired private PaymentDAO paymentDAO;
    @Autowired private CostumeDAO costumeDAO;
    @Autowired private PromotionDAO promotionDAO;

    @Transactional
    public Booking confirmBooking(BookingRequestDTO req) {
        Customer customer = customerDAO.findById(req.getCustomerId());
        if (customer == null) throw new RuntimeException("Khách hàng không tồn tại!");

        Cart cart = cartDAO.findByCustomerId(req.getCustomerId());
        if(cart == null || cart.getItems().isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setRentalDate(req.getRentalDate());
        booking.setReturnDateExpected(req.getReturnDate());
        booking.setStatus("Confirmed");

        double totalItemPrice = cart.getItems().stream()
                .mapToDouble(i -> i.getCostume().getRentalPrice() * i.getQuantity())
                .sum();
        booking.setTotalAmount(totalItemPrice);

        bookingDAO.save(booking);

        for (CartItem item : cart.getItems()) {
            BookingDetail detail = new BookingDetail();
            detail.setBooking(booking);
            detail.setCostume(item.getCostume());
            detail.setQuantity(item.getQuantity());
            detail.setPriceAtBooking(item.getCostume().getRentalPrice());

            bookingDetailDAO.save(detail);

            Costume costume = item.getCostume();
            costume.setQuantityAvailable(costume.getQuantityAvailable() - item.getQuantity());
            costumeDAO.updateCostume(costume); // Hàm này giả định đã có trong CostumeDAO
        }

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

        billDAO.createBill(bill);

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(deposit);
        payment.setPaymentDate(new Date());
        payment.setNote("Deposit (Cọc)");

        paymentDAO.createPayment(payment);

        cart.getItems().clear();
        cartDAO.save(cart);

        return booking;
    }
}