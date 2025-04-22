package org.pdv.service.product;

import org.pdv.domain.product.Product;

import java.util.List;

public interface ProductRepository {
    public String save(Product category);

    public void delete(String name);

    public List<Product> findAll();

    public Product findById(String id);

    public boolean existsByName(String name);

    public void update(Product category);
}
