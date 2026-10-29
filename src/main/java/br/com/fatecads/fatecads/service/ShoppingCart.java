package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Product;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/** Session data contains product IDs and quantities only; prices always come from the database. */
@Component
@SessionScope
public class ShoppingCart implements Serializable {
    private final Map<Integer, Integer> quantities = new LinkedHashMap<>();
    public synchronized void add(Integer productId, int quantity) {
        if (productId == null) throw new IllegalArgumentException("Choose a product.");
        checkQuantity(quantity);
        int updated = quantities.getOrDefault(productId, 0) + quantity;
        checkQuantity(updated);
        if (!quantities.containsKey(productId) && quantities.size() >= 100)
            throw new IllegalArgumentException("Your cart can contain up to 100 different products.");
        quantities.put(productId, updated);
    }
    public synchronized void update(Integer productId, int quantity) {
        checkQuantity(quantity);
        if (!quantities.containsKey(productId)) throw new IllegalArgumentException("This product is not in your cart.");
        quantities.put(productId, quantity);
    }
    public synchronized void remove(Integer productId) { quantities.remove(productId); }
    public synchronized void clear() { quantities.clear(); }
    public synchronized Map<Integer, Integer> snapshot() { return new LinkedHashMap<>(quantities); }
    public static void checkQuantity(Integer quantity) {
        if (quantity == null || quantity < 1 || quantity > 999)
            throw new IllegalArgumentException("Quantity must be a whole number between 1 and 999.");
    }
    public static BigDecimal price(Product product) {
        if (product == null || product.getProductPrice() == null || !Double.isFinite(product.getProductPrice())
                || product.getProductPrice() < 0)
            throw new IllegalArgumentException("A product is unavailable or has an invalid price. Remove it from your cart.");
        return BigDecimal.valueOf(product.getProductPrice()).setScale(2, RoundingMode.HALF_UP);
    }
    public record Line(Integer productId, String description, String imagePath, int quantity,
                       BigDecimal price, BigDecimal subtotal, boolean available) {}
}
