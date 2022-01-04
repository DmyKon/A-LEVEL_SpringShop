package ua.konstatnytov.alevel.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerInfo {
    private String name;
    private String address;
    private String email;
    private String phone;
    private boolean valid;

    public CustomerInfo(CustomerForm customerForm) {
        name = customerForm.getName();
        address = customerForm.getAddress();
        email = customerForm.getEmail();
        phone = customerForm.getPhone();
        valid = customerForm.isValid();
    }
}