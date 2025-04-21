package org.pdv.service.brand;

import org.pdv.domain.brand.Brand;

import java.util.List;

public interface BrandRepository {
    public String save(Brand brand);

    public void delete(String name);

    public List<Brand> findAll();

    public Brand findById(String id);

    public boolean existsByName(String name);

    public void update(Brand brand);
}
