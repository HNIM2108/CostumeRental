package com.kttk.CostumeRental.entity;

import com.fasterxml.jackson.annotation.JsonProperty; // Thêm import
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "tbl_promotion")
@Data
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "discount_percent")
    @JsonProperty("discountPercent")
    private double discountPercent;

    @Column(name = "min_order_value")
    @JsonProperty("minOrderValue")
    private double minOrderValue;

    @Column(name = "start_date")
    @JsonProperty("start_date")
    private Date startDate;

    @Column(name = "end_date")
    @JsonProperty("end_date")
    private Date endDate;
}