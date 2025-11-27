package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "tbl_costume")
@Data
public class Costume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String size;
    private String color;

    @Column(name = "rental_price")
    private Double rentalPrice;

    @Column(name = "deposit_price")
    private Double depositPrice;

    @Column(name = "quantity_available")
    private int quantityAvailable;

    @Column(name = "image_url")
    private String imageUrl;

    private String status;

     @ManyToOne
     @JoinColumn(name = "category_id")
     private Category category;
}