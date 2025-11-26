package com.kttk.CostumeRental.DAO;

import com.kttk.CostumeRental.entity.Costume;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class CostumeDAO extends DAO {
    public List<Costume> getAll() {
        return entityManager.createQuery("SELECT c FROM Costume c", Costume.class).getResultList();
    }
    public List<Costume> search(String name) {
        return entityManager.createQuery("SELECT c FROM Costume c WHERE c.name LIKE :name", Costume.class)
                .setParameter("name", "%" + name + "%").getResultList();
    }
    public Costume getById(Long id) { return entityManager.find(Costume.class, id); }
}