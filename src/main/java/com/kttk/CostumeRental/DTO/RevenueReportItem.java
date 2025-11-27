package com.kttk.CostumeRental.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueReportItem {
    private Long customerId;
    private String customerName;
    private Double totalRevenue;
    private Long transactionCount;
}