package com.kttk.CostumeRental.DAO;

import com.kttk.CostumeRental.DTO.RevenueReportItem;
import com.kttk.CostumeRental.entity.Payment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class PaymentDAO extends DAO {
    public void createPayment(Payment p) { entityManager.persist(p); }
    public List<RevenueReportItem> getRevenueByPayment(Date start, Date end) {
        String jpql = "SELECT new com.kttk.CostumeRental.DTO.RevenueReportItem(" +
                "c.id, c.fullName, SUM(p.amount), COUNT(p)) " +
                "FROM Payment p " +
                "JOIN p.bill b " +
                "JOIN b.booking bk " +
                "JOIN bk.customer c " +
                "WHERE p.paymentDate BETWEEN :start AND :end " +
                "GROUP BY c.id, c.fullName " +
                "ORDER BY SUM(p.amount) DESC";

        return entityManager.createQuery(jpql, RevenueReportItem.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}