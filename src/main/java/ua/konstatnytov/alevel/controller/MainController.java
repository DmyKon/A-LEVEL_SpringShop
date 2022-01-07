package ua.konstatnytov.alevel.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ua.konstatnytov.alevel.dao.OrderDAO;
import ua.konstatnytov.alevel.dao.ProductDAO;
import ua.konstatnytov.alevel.entity.Product;
import ua.konstatnytov.alevel.model.CustomerForm;
import ua.konstatnytov.alevel.model.CartInfo;
import ua.konstatnytov.alevel.model.CustomerInfo;
import ua.konstatnytov.alevel.model.ProductInfo;
import ua.konstatnytov.alevel.pagination.PaginationResult;
import ua.konstatnytov.alevel.service.CartService;
import ua.konstatnytov.alevel.validator.CustomerFormValidator;

@Controller
@Transactional
public class MainController {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CustomerFormValidator customerFormValidator;

    @InitBinder
    public void myInitBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        if (target.getClass() != CartInfo.class) {
            if (target.getClass() == CustomerForm.class) {
                dataBinder.setValidator(customerFormValidator);
            }
        }
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "/403";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/productList")
    public String listProductHandler(
            Model model,
            @RequestParam(value = "name", defaultValue = "") String likeName,
            @RequestParam(value = "sort", defaultValue = "createDate") String sortingBy,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        final int maxResult = 6;
        final int maxNavigationPage = 8;
        PaginationResult<ProductInfo> result = productDAO
                .queryProducts(page, maxResult, maxNavigationPage, likeName, sortingBy);
        model.addAttribute("paginationProducts", result);
        return "productList";
    }

    @GetMapping("/buyProduct")
    public String listProductHandler(
            HttpServletRequest request,
            @RequestParam(value = "code", defaultValue = "") String code) {
        Product product = null;
        if (code != null && code.length() > 0) {
            product = productDAO.findProduct(code);
        }
        if (product != null) {
            CartInfo cartInfo = CartService.getCartInSession(request);
            ProductInfo productInfo = new ProductInfo(product);
            cartInfo.addProduct(productInfo, 1);
        }
        return "redirect:/shoppingCart";
    }

    @GetMapping("/shoppingCartRemoveProduct")
    public String removeProductHandler(
            HttpServletRequest request,
            @RequestParam(value = "code", defaultValue = "") String code) {
        Product product = null;
        if (code != null && code.length() > 0) {
            product = productDAO.findProduct(code);
        }
        if (product != null) {
            CartInfo cartInfo = CartService.getCartInSession(request);
            ProductInfo productInfo = new ProductInfo(product);
            cartInfo.removeProduct(productInfo);
        }
        return "redirect:/shoppingCart";
    }

    @PostMapping("/shoppingCart")
    public String shoppingCartUpdateQty(
            HttpServletRequest request,
            @ModelAttribute("cartForm") CartInfo cartForm) {
        CartInfo cartInfo = CartService.getCartInSession(request);
        cartInfo.updateQuantity(cartForm);
        return "redirect:/shoppingCart";
    }

    @GetMapping("/shoppingCart")
    public String shoppingCartHandler(HttpServletRequest request, Model model) {
        CartInfo myCart = CartService.getCartInSession(request);
        model.addAttribute("cartForm", myCart);
        return "shoppingCart";
    }

    @GetMapping("/shoppingCartCustomer")
    public String shoppingCartCustomerForm(HttpServletRequest request, Model model) {
        CartInfo cartInfo = CartService.getCartInSession(request);
        if (cartInfo.isEmpty()) {
            return "redirect:/shoppingCart";
        }
        CustomerInfo customerInfo = cartInfo.getCustomerInfo();
        CustomerForm customerForm = new CustomerForm(customerInfo);
        model.addAttribute("customerForm", customerForm);
        return "shoppingCartCustomer";
    }

    @PostMapping("/shoppingCartCustomer")
    public String shoppingCartCustomerSave(
            HttpServletRequest request,
            @ModelAttribute("customerForm") @Validated CustomerForm customerForm,
            BindingResult result) {
        if (result.hasErrors()) {
            customerForm.setValid(false);
            return "shoppingCartCustomer";
        }
        customerForm.setValid(true);
        CartInfo cartInfo = CartService.getCartInSession(request);
        CustomerInfo customerInfo = new CustomerInfo(customerForm);
        cartInfo.setCustomerInfo(customerInfo);
        return "redirect:/shoppingCartConfirmation";
    }

    @GetMapping("/shoppingCartConfirmation")
    public String shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
        CartInfo cartInfo = CartService.getCartInSession(request);
        if (cartInfo.isEmpty()) {
            return "redirect:/shoppingCart";
        } else if (!cartInfo.isValidCustomer()) {
            return "redirect:/shoppingCartCustomer";
        }
        model.addAttribute("myCart", cartInfo);
        return "shoppingCartConfirmation";
    }

    @PostMapping("/shoppingCartConfirmation")
    public String shoppingCartConfirmationSave(HttpServletRequest request) {
        CartInfo cartInfo = CartService.getCartInSession(request);
        if (cartInfo.isEmpty()) {
            return "redirect:/shoppingCart";
        } else if (!cartInfo.isValidCustomer()) {
            return "redirect:/shoppingCartCustomer";
        }
        try {
            orderDAO.saveOrder(cartInfo);
        } catch (Exception e) {
            return "shoppingCartConfirmation";
        }
        CartService.removeCartInSession(request);
        CartService.storeLastOrderedCartInSession(request, cartInfo);
        return "redirect:/shoppingCartFinalize";
    }

    @GetMapping("/shoppingCartFinalize")
    public String shoppingCartFinalize(HttpServletRequest request, Model model) {
        CartInfo lastOrderedCart = CartService.getLastOrderedCartInSession(request);
        if (lastOrderedCart == null) {
            return "redirect:/shoppingCart";
        }
        model.addAttribute("lastOrderedCart", lastOrderedCart);
        return "shoppingCartFinalize";
    }

    @GetMapping("/productImage")
    public void productImage(HttpServletResponse response, @RequestParam("code") String code) throws IOException {
        Product product = null;
        if (code != null) {
            product = this.productDAO.findProduct(code);
        }
        if (product != null && product.getImage() != null) {
            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            response.getOutputStream().write(product.getImage());
        }
        response.getOutputStream().close();
    }
}