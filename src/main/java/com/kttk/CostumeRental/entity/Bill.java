package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "tbl_bill")
@Data
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "total_item_amount")
    private Double totalItemAmount;

    @Column(name = "penalty_amount")
    private Double penaltyAmount = 0.0;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "final_amount")
    private Double finalAmount;

    @Column(name = "deposit_amount")
    private Double depositAmount;

    private String status;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "created_date")
    private Date createdDate = new Date();

    @Column(name = "return_date_actual")
    private Date returnDateActual;
}