package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.*;
import br.com.fatecads.fatecads.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class CheckoutService {
    private final ProductRepository products;
    private final UserRepository users;
    private final OrderRepository orders;
    public CheckoutService(ProductRepository products, UserRepository users, OrderRepository orders) {
        this.products = products; this.users = users; this.orders = orders;
    }
    @Transactional
    public Order checkout(Map<Integer, Integer> quantities, String login) {
        if (quantities.isEmpty()) throw new IllegalArgumentException("Add at least one product before placing your order.");
        Order order = new Order();
        order.setUser(users.findByLogin(login).orElseThrow(() -> new IllegalArgumentException("Account not found.")));
        order.setOrderDate(LocalDate.now());
        List<ItemOfOrder> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (var entry : quantities.entrySet()) {
            ShoppingCart.checkQuantity(entry.getValue());
            Product product = products.findById(entry.getKey()).orElseThrow(() ->
                    new IllegalArgumentException("A product is no longer available. Remove it from your cart."));
            BigDecimal price = ShoppingCart.price(product);
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(entry.getValue()));
            ItemOfOrder item = new ItemOfOrder();
            item.setProduct(product); item.setAmount(entry.getValue());
            item.setPrice(price.doubleValue()); item.setSubtotal(subtotal.doubleValue()); item.setOrder(order);
            items.add(item); total = total.add(subtotal);
        }
        order.setItem(items); order.setOrderTotal(total.doubleValue());
        return orders.save(order);
    }
}
