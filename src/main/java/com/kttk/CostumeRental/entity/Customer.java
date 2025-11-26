package com.kttk.CostumeRental.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tbl_customer")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends User {
    @Column(name = "loyalty_points")
    private int loyaltyPoints = 0;
}