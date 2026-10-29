package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.ItemOfOrder;
import br.com.fatecads.fatecads.repository.ItemOfOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemOfOrderService {

    @Autowired
    private ItemOfOrderRepository itemOfOrderRepository;

    @Autowired
    private br.com.fatecads.fatecads.repository.OrderRepository orders;
    @Autowired
    private br.com.fatecads.fatecads.repository.ProductRepository products;

    //Method to save one order item
    @org.springframework.transaction.annotation.Transactional
    public ItemOfOrder save(ItemOfOrder itemOfOrder) {
        ShoppingCart.checkQuantity(itemOfOrder.getAmount());
        Integer oldOrderId = null;
        if (itemOfOrder.getItemId() != null) {
            ItemOfOrder existing = itemOfOrderRepository.findById(itemOfOrder.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Order item not found."));
            checkEditable(existing.getOrder());
            oldOrderId = existing.getOrder() == null ? null : existing.getOrder().getOrderId();
        }
        if (itemOfOrder.getOrder() == null || itemOfOrder.getOrder().getOrderId() == null)
            throw new IllegalArgumentException("Select an order.");
        var order = orders.findById(itemOfOrder.getOrder().getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
        checkEditable(order);
        if (itemOfOrder.getProduct() == null || itemOfOrder.getProduct().getProductId() == null)
            throw new IllegalArgumentException("Select a product.");
        var product = products.findById(itemOfOrder.getProduct().getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
        itemOfOrder.setOrder(order); itemOfOrder.setProduct(product);
        itemOfOrder.setPrice(ShoppingCart.price(product).doubleValue());
        itemOfOrder.subTotalUpdate();
        ItemOfOrder saved = itemOfOrderRepository.saveAndFlush(itemOfOrder);
        updateTotal(order.getOrderId());
        if (oldOrderId != null && !oldOrderId.equals(order.getOrderId())) updateTotal(oldOrderId);
        return saved;
    }

    //Method to search all order items
    public List<ItemOfOrder> findAll() {
        return itemOfOrderRepository.findAll();
    }

    //Method to search order items by ID
    public ItemOfOrder findById(Integer id) {
        return itemOfOrderRepository.findById(id).orElse(null);
    }

    //Method to delete one order item by ID
    @org.springframework.transaction.annotation.Transactional
    public void deleteById(Integer id) {
        ItemOfOrder item = itemOfOrderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order item not found."));
        checkEditable(item.getOrder());
        Integer orderId = item.getOrder() == null ? null : item.getOrder().getOrderId();
        if (item.getOrder() != null && item.getOrder().getItem() != null)
            item.getOrder().getItem().removeIf(current -> current.getItemId().equals(id));
        itemOfOrderRepository.delete(item);
        itemOfOrderRepository.flush();
        if (orderId != null) updateTotal(orderId);
    }

    private void checkEditable(br.com.fatecads.fatecads.entity.Order order) {
        if (order != null && order.getUser() != null)
            throw new IllegalArgumentException("Confirmed customer orders cannot be changed.");
    }

    private void updateTotal(Integer id) {
        var order = orders.findById(id).orElseThrow();
        java.math.BigDecimal total = itemOfOrderRepository.findByOrderOrderId(id).stream()
                .map(item -> java.math.BigDecimal.valueOf(item.getSubtotal()))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        order.setOrderTotal(total.doubleValue());
        orders.save(order);
    }
}
