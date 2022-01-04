package ua.konstatnytov.alevel.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.konstatnytov.alevel.entity.Product;

@Getter
@Setter
@NoArgsConstructor
public class ProductInfo {
    private String code;
    private String name;
    private double price;

    public ProductInfo(Product product) {
        code = product.getCode();
        name = product.getName();
        price = product.getPrice();
    }

    public ProductInfo(String code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }
}