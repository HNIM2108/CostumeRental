package com.kttk.CostumeRental.pattern.command;

import com.kttk.CostumeRental.service.CartService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RemoveFromCartCommand implements ICommand {
    private CartService cartService;
    private Long cartItemId;

    @Override
    public Object execute() {
        cartService.removeItem(cartItemId);
        return "Đã xóa sản phẩm khỏi giỏ";
    }
}