package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.ItemOfOrder;
import br.com.fatecads.fatecads.entity.Order;
import br.com.fatecads.fatecads.entity.Product;
import br.com.fatecads.fatecads.repository.OrderRepository;
import br.com.fatecads.fatecads.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    //Method to create the order
    public Order orderCreate(Order order){
        order.setOrderDate(LocalDate.now());
        for(ItemOfOrder item : order.getItem()){
            Product product = productRepository.findById(item.getProduct().getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            item.setProduct(product);
            item.setPrice(product.getProductPrice());
            item.subTotalUpdate();
            item.setOrder(order);
        }
        order.totalUpdate();
        return orderRepository.save(order);
    }

    //Method to search all orders
    public java.util.List<Order> findAll(){
        return orderRepository.findAll();
    }
}
