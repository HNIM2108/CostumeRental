package com.kttk.CostumeRental.DTO;

import lombok.Data;
import java.util.Date;

@Data
public class BookingRequestDTO {
    private Long customerId;
    private Date rentalDate;
    private Date returnDate;
}