package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity @Table(name = "tbl_booking")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Date rentalDate;
    private Date returnDateExpected;
    private String status; // Pending, Confirmed

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Bill bill;
}