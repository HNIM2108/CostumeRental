package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tbl_booking")
@Data
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(name = "created_date")
    private Date createdDate = new Date();

    @Column(name = "rental_date")
    private Date rentalDate;

    @Column(name = "return_date_expected")
    private Date returnDateExpected;

    @Column(name = "total_amount")
    private Double totalAmount;

    private String status;
    private String note;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Bill bill;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingDetail> bookingDetails;
}