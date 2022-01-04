package ua.konstatnytov.alevel.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartLineInfo {
    private ProductInfo productInfo;
    private int quantity;

    public CartLineInfo() {
        this.quantity = 0;
    }

    public double getAmount() {
        return productInfo.getPrice() * quantity;
    }
}