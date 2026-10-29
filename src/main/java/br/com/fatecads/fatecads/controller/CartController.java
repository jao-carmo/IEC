package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.*;
import br.com.fatecads.fatecads.repository.OrderRepository;
import br.com.fatecads.fatecads.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.security.Principal;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class CartController {
    private final ShoppingCart cart;
    private final ProductService products;
    private final CheckoutService checkout;
    private final OrderRepository orders;
    public CartController(ShoppingCart cart, ProductService products, CheckoutService checkout, OrderRepository orders) {
        this.cart = cart; this.products = products; this.checkout = checkout; this.orders = orders;
    }
    @GetMapping("/cart")
    public String view(Model model) {
        List<ShoppingCart.Line> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        boolean available = true;
        for (var entry : cart.snapshot().entrySet()) {
            Product product = products.findById(entry.getKey());
            try {
                BigDecimal price = ShoppingCart.price(product);
                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(entry.getValue()));
                lines.add(new ShoppingCart.Line(entry.getKey(), product.getProductDescription(), product.getImagePath(),
                        entry.getValue(), price, subtotal, true));
                total = total.add(subtotal);
            } catch (IllegalArgumentException ex) {
                available = false;
                lines.add(new ShoppingCart.Line(entry.getKey(), "Unavailable product", null, entry.getValue(),
                        BigDecimal.ZERO, BigDecimal.ZERO, false));
            }
        }
        model.addAttribute("lines", lines); model.addAttribute("total", total);
        model.addAttribute("canCheckout", available && !lines.isEmpty());
        return "cart/cart";
    }
    @PostMapping("/cart/add")
    public String add(@RequestParam Integer productId, @RequestParam(defaultValue="1") int quantity, RedirectAttributes flash) {
        try {
            ShoppingCart.price(products.findById(productId)); cart.add(productId, quantity);
            flash.addFlashAttribute("successMessage", "Product added to your cart.");
        } catch (IllegalArgumentException ex) { flash.addFlashAttribute("errorMessage", ex.getMessage()); }
        return "redirect:/cart";
    }
    @PostMapping("/cart/update")
    public String update(@RequestParam Integer productId, @RequestParam int quantity, RedirectAttributes flash) {
        try { cart.update(productId, quantity); }
        catch (IllegalArgumentException ex) { flash.addFlashAttribute("errorMessage", ex.getMessage()); }
        return "redirect:/cart";
    }
    @PostMapping("/cart/remove")
    public String remove(@RequestParam Integer productId) { cart.remove(productId); return "redirect:/cart"; }
    @PostMapping("/cart/clear")
    public String clear() { cart.clear(); return "redirect:/cart"; }
    @PostMapping("/cart/checkout")
    public String checkout(Principal principal, RedirectAttributes flash) {
        synchronized (cart) {
            try {
                Order order = checkout.checkout(cart.snapshot(), principal.getName());
                cart.clear();
                flash.addFlashAttribute("successMessage", "Order #" + order.getOrderId() + " placed successfully.");
                return "redirect:/purchases";
            } catch (IllegalArgumentException ex) { flash.addFlashAttribute("errorMessage", ex.getMessage()); }
        }
        return "redirect:/cart";
    }
    @GetMapping("/purchases")
    public String purchases(Principal principal, Model model) {
        model.addAttribute("orders", orders.findByUserLoginOrderByOrderIdDesc(principal.getName()));
        return "cart/purchases";
    }
    @GetMapping("/purchases/{id}")
    public String detail(@PathVariable Integer id, Principal principal, Model model) {
        Order order = orders.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (order.getUser() == null || !order.getUser().getLogin().equals(principal.getName()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        model.addAttribute("order", order);
        return "cart/receipt";
    }
}
