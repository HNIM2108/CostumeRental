package com.kttk.CostumeRental.service;

import com.kttk.CostumeRental.DAO.BillDAO;
import com.kttk.CostumeRental.DAO.PaymentDAO;
import com.kttk.CostumeRental.DTO.RevenueReportItem;
import com.kttk.CostumeRental.pattern.strategy.IStatisticStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class StatisticService {

    @Autowired private PaymentDAO paymentDAO;
    @Autowired private BillDAO billDAO;

    private IStatisticStrategy strategy; // Chiến lược hiện tại

    public void setStrategy(IStatisticStrategy strategy) {
        this.strategy = strategy;
    }

    public List<RevenueReportItem> generateReport(Date start, Date end) {
        if (strategy == null) {
            throw new RuntimeException("Chưa chọn loại báo cáo!");
        }
        // Ủy quyền cho Strategy tính toán, truyền DAO vào
        return strategy.calculateRevenue(start, end, paymentDAO, billDAO);
    }
}