package br.com.fatecads.fatecads.controller;


import br.com.fatecads.fatecads.entity.Order;
import br.com.fatecads.fatecads.entity.Product;
import br.com.fatecads.fatecads.entity.Student;
import br.com.fatecads.fatecads.service.OrderService;
import br.com.fatecads.fatecads.service.ProductService;
import br.com.fatecads.fatecads.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProductService productService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<Void> save(@RequestBody Order order) {
        orderService.orderCreate(order);
        return ResponseEntity.ok().build();
    }

    //Method to open the order creation view
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("order", new Order());
        //Student
        List<Student> students = studentService.findAll();
        model.addAttribute("students", students);
        //Product
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "order/orderForm";
    }
}
