package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "tbl_bill")
@Data
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private double totalAmount;
    private double depositAmount; // Tiền cọc
    private String status; // Unpaid, PartiallyPaid

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Payment> payments;
}