package com.kttk.CostumeRental.pattern.command;

import com.kttk.CostumeRental.service.CartService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddToCartCommand implements ICommand {
    private CartService cartService;
    private Long customerId;
    private Long costumeId;
    private int quantity;

    @Override
    public Object execute() {
        cartService.addToCart(customerId, costumeId, quantity);
        return "Added to cart";
    }
}