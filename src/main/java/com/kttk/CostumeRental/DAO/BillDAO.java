package com.kttk.CostumeRental.DAO;

import com.kttk.CostumeRental.DTO.RevenueReportItem;
import com.kttk.CostumeRental.entity.Bill;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class BillDAO extends DAO {

    // ... (các hàm save cũ) ...
    public void createBill(Bill b) { entityManager.persist(b); }

    // --- HÀM MỚI: Thống kê Doanh số ---
    public List<RevenueReportItem> getRevenueByBill(Date start, Date end) {
        String jpql = "SELECT new com.kttk.CostumeRental.dto.RevenueReportItem(" +
                "c.id, c.fullName, SUM(b.finalAmount), COUNT(b)) " +
                "FROM Bill b " +
                "JOIN b.booking bk " +
                "JOIN bk.customer c " +
                "WHERE b.createdDate BETWEEN :start AND :end " +
                "GROUP BY c.id, c.fullName " +
                "ORDER BY SUM(b.finalAmount) DESC";

        return entityManager.createQuery(jpql, RevenueReportItem.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}