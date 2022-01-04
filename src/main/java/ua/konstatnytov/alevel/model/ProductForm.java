package ua.konstatnytov.alevel.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ua.konstatnytov.alevel.entity.Product;

@Getter
@Setter
public class ProductForm {
    private String code;
    private String name;
    private double price;
    private boolean newProduct = false;
    private MultipartFile fileData;

    public ProductForm() {
        newProduct = true;
    }

    public ProductForm(Product product) {
        code = product.getCode();
        name = product.getName();
        price = product.getPrice();
    }
}