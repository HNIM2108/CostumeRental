package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_penalty")
@Data
public class Penalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private Double amount;
}