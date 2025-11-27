package com.kttk.CostumeRental.pattern.strategy;

import com.kttk.CostumeRental.DAO.BillDAO;
import com.kttk.CostumeRental.DAO.PaymentDAO;
import com.kttk.CostumeRental.DTO.RevenueReportItem;
import java.util.Date;
import java.util.List;

public class CashFlowStrategy implements IStatisticStrategy {
    @Override
    public List<RevenueReportItem> calculateRevenue(Date start, Date end, PaymentDAO pDao, BillDAO bDao) {
        return pDao.getRevenueByPayment(start, end);
    }
}