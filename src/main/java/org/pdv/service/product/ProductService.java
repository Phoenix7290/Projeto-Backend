package org.pdv.service.product;

import org.pdv.domain.error.DomainException;
import org.pdv.domain.product.Product;
import org.pdv.service.brand.BrandOutput;
import org.pdv.service.brand.BrandRepository;
import org.pdv.service.category.CategoryOutput;
import org.pdv.service.category.CategoryRepository;

import java.util.List;

public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    public ProductOutput createProduct(ProductInput aProduct) throws DomainException {
        final var product = Product.with(
                aProduct.name(),
                aProduct.description(),
                aProduct.price(),
                aProduct.stock(),
                aProduct.categoryId(),
                aProduct.brandId()
        );
        product.validate();

        final var exist = productRepository.existsByName(product.getName());
        if (exist) {
            throw new IllegalArgumentException("'name' already exists");
        }

        final var category = categoryRepository.findById(product.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("'categoryId' not found");
        }

        final var brand = brandRepository.findById(product.getBrandId());
        if (brand == null) {
            throw new IllegalArgumentException("'brandId' not found");
        }

        productRepository.save(product);

        return new ProductOutput(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                new CategoryOutput(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                ),
                new BrandOutput(
                        brand.getId(),
                        brand.getName(),
                        brand.getDescription()
                )
        );
    }

    public void deleteProduct(String id) {
        productRepository.delete(id);
    }

    public List<ProductOutput> listAllProducts() {
        final var products = productRepository.findAll();

        return products.stream()
                .map(product -> {
                    final var category = categoryRepository.findById(product.getCategoryId());
                    final var brand = brandRepository.findById(product.getBrandId());

                    return new ProductOutput(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            product.getStock(),
                            new CategoryOutput(
                                    product.getCategoryId(),
                                    category.getName(),
                                    category.getDescription()
                            ),
                            new BrandOutput(
                                    product.getBrandId(),
                                    brand.getName(),
                                    brand.getDescription()
                            )
                    );
                })
                .toList();
    }

    public ProductOutput getProduct(String id) {
        final var product = productRepository.findById(id);

        if (product == null) {
            return null;
        }

        final var category = categoryRepository.findById(product.getCategoryId());
        final var brand = brandRepository.findById(product.getBrandId());

        return new ProductOutput(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                new CategoryOutput(
                        product.getCategoryId(),
                        category.getName(),
                        category.getDescription()
                ),
                new BrandOutput(
                        product.getBrandId(),
                        brand.getName(),
                        brand.getDescription()
                )
        );
    }

    public ProductOutput updateProduct(String id, ProductInput aProduct) throws DomainException {
        final var product = productRepository.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("'id' not found");
        }

        product.setName(aProduct.name());
        product.setDescription(aProduct.description());
        product.setPrice(aProduct.price());
        product.setStock(aProduct.stock());
        product.setCategoryId(aProduct.categoryId());
        product.setBrandId(aProduct.brandId());

        product.validate();

        final var category = categoryRepository.findById(product.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("'categoryId' not found");
        }

        final var brand = brandRepository.findById(product.getBrandId());
        if (brand == null) {
            throw new IllegalArgumentException("'brandId' not found");
        }

        productRepository.update(product);

        return new ProductOutput(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                new CategoryOutput(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                ),
                new BrandOutput(
                        brand.getId(),
                        brand.getName(),
                        brand.getDescription()
                )
        );
    }
}
