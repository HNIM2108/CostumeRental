package com.kttk.CostumeRental.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public abstract class DAO {

    @PersistenceContext
    protected EntityManager entityManager;
}