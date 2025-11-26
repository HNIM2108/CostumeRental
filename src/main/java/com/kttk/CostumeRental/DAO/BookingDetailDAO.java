package com.kttk.CostumeRental.DAO;

import com.kttk.CostumeRental.entity.BookingDetail;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BookingDetailDAO extends DAO {
    public void save(BookingDetail detail) {
        if(detail.getId() == null) entityManager.persist(detail);
        else entityManager.merge(detail);
    }
}