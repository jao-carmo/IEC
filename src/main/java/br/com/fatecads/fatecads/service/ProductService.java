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
        validate(product);
        return productRepository.save(product);
    }

    //Method to save one product and its image
    public Product save(Product product, MultipartFile image) throws IOException {
        validate(product);
        Product existing = product.getProductId() == null ? null : productRepository.findById(product.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
        product.setImagePath(existing == null ? null : existing.getImagePath());
        product.setItem(null);
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("Images must be smaller than 5 MB.");
            String extension;
            try (var stream = javax.imageio.ImageIO.createImageInputStream(image.getInputStream())) {
                var readers = javax.imageio.ImageIO.getImageReaders(stream);
                if (!readers.hasNext()) throw new IllegalArgumentException("Upload a valid PNG or JPEG image.");
                var reader = readers.next();
                try {
                    reader.setInput(stream);
                    String format = reader.getFormatName().toLowerCase(java.util.Locale.ROOT);
                    if (!format.equals("png") && !format.equals("jpeg")) throw new IllegalArgumentException("Only PNG and JPEG images are allowed.");
                    if ((long) reader.getWidth(0) * reader.getHeight(0) > 20_000_000)
                        throw new IllegalArgumentException("Image dimensions are too large.");
                    reader.read(0);
                    extension = format.equals("png") ? ".png" : ".jpg";
                } finally { reader.dispose(); }
            }

            Path directory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            String fileName = UUID.randomUUID() + extension;
            image.transferTo(directory.resolve(fileName));
            product.setImagePath("/uploads/" + fileName);
        }

        return productRepository.save(product);
    }

    private void validate(Product product) {
        if (product.getProductDescription() == null || product.getProductDescription().isBlank() || product.getProductDescription().length() > 40
                || product.getProductBrand() == null || product.getProductBrand().isBlank() || product.getProductBrand().length() > 30
                || product.getProductUnit() == null || product.getProductUnit().isBlank() || product.getProductUnit().length() > 10)
            throw new IllegalArgumentException("Enter a description, brand and unit within the field limits.");
        if (ShoppingCart.price(product).compareTo(new java.math.BigDecimal("99999999.99")) > 0)
            throw new IllegalArgumentException("Product price must not exceed 99,999,999.99.");
        product.setProductPrice(ShoppingCart.price(product).doubleValue());
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
