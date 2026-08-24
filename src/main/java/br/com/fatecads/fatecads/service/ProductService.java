package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Product;
import br.com.fatecads.fatecads.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    //Method to save one product
    public Product save(Product product){
        return productRepository.save(product);
    }

    //Method to search all products
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    //Method to search products for ID
    public Product findById(Integer id){
        return productRepository.findById(id).orElse(null);
    }

    //Method to delete one product of ID
    public void deleteById(Integer id){
        productRepository.deleteById(id);
    }
}
