package org.pdv.service.brand;

import org.pdv.domain.brand.Brand;
import org.pdv.domain.error.DomainException;

import java.util.List;

public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public BrandOutput createBrand(BrandInput aBrand) throws DomainException {
        final var brand = Brand.with(aBrand.name(), aBrand.description());
        brand.validate();

        final var exist = brandRepository.existsByName(brand.getName());
        if (exist) {
            throw new IllegalArgumentException("'name' already exists");
        }

        brandRepository.save(brand);

        return new BrandOutput(
                brand.getId(),
                brand.getName(),
                brand.getDescription()
        );
    }

    public void deleteBrand(String name) {
        brandRepository.delete(name);
    }

    public List<BrandOutput> listAllBrands() {
        final var brands = brandRepository.findAll();

        return brands.stream()
                .map(brand -> new BrandOutput(
                        brand.getId(),
                        brand.getName(),
                        brand.getDescription()))
                .toList();
    }

    public BrandOutput getBrand(String id) {
        final var brand = brandRepository.findById(id);

        return new BrandOutput(
                brand.getId(),
                brand.getName(),
                brand.getDescription());
    }

    public BrandOutput updateBrand(String id, BrandInput brandInput) throws DomainException {
        final var brand = brandRepository.findById(id);

        brand.setName(brandInput.name());
        brand.setDescription(brandInput.description());

        brand.validate();

        brandRepository.update(brand);

        return new BrandOutput(
                brand.getId(),
                brand.getName(),
                brand.getDescription());
    }
}
