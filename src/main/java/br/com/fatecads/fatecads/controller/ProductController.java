package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Product;
import br.com.fatecads.fatecads.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    //Method to list all products
    @GetMapping("/list")
    public String list(Model model, @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "all") String budget, @RequestParam(defaultValue = "featured") String sort){
        List<Product> products = productService.findAll();
        model.addAttribute("productCount", products.size());
        String query = q.trim().toLowerCase(java.util.Locale.ROOT);
        var filtered = products.stream().filter(p -> query.isEmpty()
                || p.getProductDescription().toLowerCase(java.util.Locale.ROOT).contains(query)
                || p.getProductBrand().toLowerCase(java.util.Locale.ROOT).contains(query))
                .filter(p -> "under200".equals(budget) ? p.getProductPrice() != null && p.getProductPrice() < 200
                        : !"200plus".equals(budget) || p.getProductPrice() != null && p.getProductPrice() >= 200);
        java.util.Comparator<Product> price = java.util.Comparator.comparing(Product::getProductPrice,
                java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
        filtered = switch (sort) {
            case "price-asc" -> filtered.sorted(price);
            case "price-desc" -> filtered.sorted(price.reversed());
            case "name" -> filtered.sorted(java.util.Comparator.comparing(Product::getProductDescription, String.CASE_INSENSITIVE_ORDER));
            default -> filtered.sorted(java.util.Comparator.comparing(Product::getProductId));
        };
        model.addAttribute("products", filtered.toList());
        model.addAttribute("q", q); model.addAttribute("budget", budget); model.addAttribute("sort", sort);
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
    public String save(@ModelAttribute Product product,
                       @RequestParam(value = "image", required = false) MultipartFile image, Model model) {
        try {
            productService.save(product, image);
            return "redirect:/product/list";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        } catch (IOException ex) {
            model.addAttribute("errorMessage", "The image could not be read or stored. Choose a valid PNG or JPEG and try again.");
        }
        return "product/productForm";
    }

    //Method to open the edit form
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model){
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product/productForm";
    }

    //Method to delete a product to ID
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.deleteById(id);
        return "redirect:/product/list";
    }
}
