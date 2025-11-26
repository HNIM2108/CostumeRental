package com.kttk.CostumeRental.DAO;

import com.kttk.CostumeRental.entity.Promotion;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Repository
@Transactional
public class PromotionDAO extends DAO {

    public Promotion findByCode(String code) {
        try {
            String jpql = "SELECT p FROM Promotion p WHERE p.code = :code";
            TypedQuery<Promotion> query = entityManager.createQuery(jpql, Promotion.class);
            query.setParameter("code", code);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
