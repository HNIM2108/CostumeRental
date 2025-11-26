package com.kttk.CostumeRental.DAO;

import com.kttk.CostumeRental.entity.Cart;
import com.kttk.CostumeRental.entity.CartItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class CartDAO extends DAO {
    public Cart findByCustomerId(Long customerId) {
        List<Cart> carts = entityManager.createQuery("SELECT c FROM Cart c WHERE c.customer.id = :cid", Cart.class)
                .setParameter("cid", customerId).getResultList();
        return carts.isEmpty() ? null : carts.get(0);
    }
    public Cart save(Cart cart) {
        if(cart.getId() == null) entityManager.persist(cart);
        else entityManager.merge(cart);
        return cart;
    }

    public void removeCartItem(Long cartItemId) {
        CartItem item = entityManager.find(CartItem.class, cartItemId);
        if (item != null) {
            // Cần xóa khỏi list của Cart cha để đồng bộ (nếu đang trong session)
            if (item.getCart() != null) {
                item.getCart().getItems().remove(item);
            }
            entityManager.remove(item);
        } else {
            throw new RuntimeException("Món đồ không tồn tại trong giỏ!");
        }
    }
}