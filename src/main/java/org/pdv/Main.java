package org.pdv;

import org.pdv.domain.error.DomainException;
import org.pdv.repository.brand.InFileBrandRepository;
import org.pdv.repository.category.InFileCategoryRepository;
import org.pdv.repository.product.InFileProductRepository;
import org.pdv.service.brand.BrandInput;
import org.pdv.service.brand.BrandService;
import org.pdv.service.category.CategoryInput;
import org.pdv.service.category.CategoryService;
import org.pdv.service.product.ProductInput;
import org.pdv.service.product.ProductService;

public class Main {
    public static void main(String[] args) {
        final var categoryRepository = new InFileCategoryRepository("db");
        final var categoryService = new CategoryService(categoryRepository);

        final var brandRepository = new InFileBrandRepository("db");
        final var brandService = new BrandService(brandRepository);

        final var productRepository = new InFileProductRepository("db");
        final var productService = new ProductService(
                productRepository,
                categoryRepository,
                brandRepository
        );

        final var categoryInput = new CategoryInput(
                "Inverno",
                "Camisetas de inverno."
        );

        final var brandInput = new BrandInput(
                "Resfriado",
                "Marca de camisetas para o frio."
        );

        try {
            final var categoryOutput = categoryService.createCategory(categoryInput);
            final var brandOutput = brandService.createBrand(brandInput);

            final var productInput = new ProductInput(
                    "Camiseta 1",
                    "Descrição muito legal da camiseta 1.",
                    120.0,
                    50,
                    categoryOutput.id(),
                    brandOutput.id()
            );

            final var productOutput = productService.createProduct(productInput);

            System.out.println(productOutput);
        } catch (DomainException e) {
            throw new RuntimeException(e);
        }
    }
}
