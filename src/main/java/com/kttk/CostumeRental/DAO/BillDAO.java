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

    // Hàm tạo hóa đơn (giữ nguyên)
    public void createBill(Bill b) {
        entityManager.persist(b);
    }

    // Hàm thống kê doanh thu theo hóa đơn (Sửa đường dẫn JPQL)
    public List<RevenueReportItem> getRevenueByBill(Date start, Date end) {
        // LƯU Ý: Trong chuỗi JPQL bên dưới, tôi đã đổi 'dto' thành 'DTO'
        String jpql = "SELECT new com.kttk.CostumeRental.DTO.RevenueReportItem(" +
                "c.id, c.fullName, CAST(SUM(b.finalAmount) AS double), COUNT(b)) " +
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