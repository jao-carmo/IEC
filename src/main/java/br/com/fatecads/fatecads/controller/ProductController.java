package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Product;
import br.com.fatecads.fatecads.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    //Method to list all products
    @GetMapping("/list")
    public String list(Model model){
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "product/productList";
    }

    //Method to open the product creation form
    @GetMapping({"/new", "/create"})
    public String createForm(Model model){
        model.addAttribute("product", new Product());
        return "product/productForm";
    }

    //Method to save a product
    @PostMapping("/save")
    public String save(@ModelAttribute Product product){
        productService.save(product);
        return "redirect:/product/list";
    }

    //Method to open the edit form
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model){
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product/productForm";
    }

    //Method to delete a product to ID
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.deleteById(id);
        return "redirect:/product/list";
    }
}
