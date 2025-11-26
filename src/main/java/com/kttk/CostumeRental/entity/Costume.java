package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_costume")
@Data
public class Costume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String size;
    private double rentalPrice;
    private int quantityAvailable;
    private String status; // Available, Rented
}
