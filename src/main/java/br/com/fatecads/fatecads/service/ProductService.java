package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Product;
import br.com.fatecads.fatecads.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDirectory;

    //Method to save one product
    public Product save(Product product){
        return productRepository.save(product);
    }

    //Method to save one product and its image
    public Product save(Product product, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            String originalName = image.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
            }

            Path directory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            String fileName = UUID.randomUUID() + extension;
            image.transferTo(directory.resolve(fileName));
            product.setImagePath("/uploads/" + fileName);
        }

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
