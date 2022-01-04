package ua.konstatnytov.alevel.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerForm {
    private String name;
    private String address;
    private String email;
    private String phone;
    private boolean valid;

    public CustomerForm(CustomerInfo customerInfo) {
        if (customerInfo != null) {
            name = customerInfo.getName();
            address = customerInfo.getAddress();
            email = customerInfo.getEmail();
            phone = customerInfo.getPhone();
        }
    }
}