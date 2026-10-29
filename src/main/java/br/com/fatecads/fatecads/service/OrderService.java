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

    @Autowired
    private br.com.fatecads.fatecads.repository.StudentRepository studentRepository;

    //Method to create the order
    @org.springframework.transaction.annotation.Transactional
    public Order orderCreate(Order order){
        if (order.getItem() == null || order.getItem().isEmpty() || order.getItem().size() > 100)
            throw new IllegalArgumentException("An order must contain between 1 and 100 items.");
        if (order.getStudent() == null || order.getStudent().getStudentId() == null)
            throw new IllegalArgumentException("Select a student.");
        order.setStudent(studentRepository.findById(order.getStudent().getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found.")));
        order.setOrderId(null);
        order.setUser(null);
        order.setOrderDate(LocalDate.now());
        for(ItemOfOrder item : order.getItem()){
            if (item == null || item.getProduct() == null || item.getProduct().getProductId() == null)
                throw new IllegalArgumentException("Select a valid product.");
            ShoppingCart.checkQuantity(item.getAmount());
            item.setItemId(null);
            Product product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found."));
            item.setProduct(product);
            item.setPrice(ShoppingCart.price(product).doubleValue());
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
