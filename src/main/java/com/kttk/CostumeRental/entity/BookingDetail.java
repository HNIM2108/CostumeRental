package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_booking_detail")
@Data
public class BookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "costume_id")
    private Costume costume;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_at_booking", nullable = false)
    private Double priceAtBooking;
}