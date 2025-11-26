package com.kttk.CostumeRental.DAO;

import com.kttk.CostumeRental.entity.CartItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CartItemDAO extends DAO {
    public void deleteById(Long id) {
        CartItem item = entityManager.find(CartItem.class, id);
        if (item != null) entityManager.remove(item);
    }

    public void save(CartItem item) {
        if(item.getId() == null) entityManager.persist(item);
        else entityManager.merge(item);
    }
}