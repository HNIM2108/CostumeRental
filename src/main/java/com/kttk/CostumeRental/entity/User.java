package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_user")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String role;
    private boolean status = true;
}