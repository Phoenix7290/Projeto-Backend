package org.pdv.service.product;

import org.pdv.domain.category.Category;
import org.pdv.domain.product.Product;

import java.util.List;

public interface ProductRepository {
    public String save(Product category);

    public void delete(String name);

    public List<Product> findAll();

    public Product findById(String id);

    public boolean existsByName(String name);

    public boolean existsById(String id);

    public void update(Product category);
}
