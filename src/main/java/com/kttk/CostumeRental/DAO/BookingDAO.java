package com.kttk.CostumeRental.DAO;

import com.kttk.CostumeRental.entity.Booking;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BookingDAO extends DAO {
    public Booking save(Booking booking) {
        if(booking.getId() == null) entityManager.persist(booking);
        else entityManager.merge(booking);
        return booking;
    }
}