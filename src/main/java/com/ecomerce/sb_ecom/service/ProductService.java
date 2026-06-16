package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.ApiException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.interfaces.IFileService;
import com.ecomerce.sb_ecom.interfaces.IPaginationService;
import com.ecomerce.sb_ecom.interfaces.IProductService;
import com.ecomerce.sb_ecom.model.Product;
import com.ecomerce.sb_ecom.payload.product.ProductDto;
import com.ecomerce.sb_ecom.payload.product.ProductResponse;
import com.ecomerce.sb_ecom.repositories.ICategoryRepository;
import com.ecomerce.sb_ecom.repositories.IProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepo;

    @Autowired
    private ICategoryRepository categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IPaginationService paginationService;

    @Autowired
    private IFileService fileService;

    @Value("${project.image}")
    private String path;


    @Override
    public ProductDto addProduct(Long categoryId, ProductDto productDto) {

        var category = categoryRepo.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();
        for (Product product : products) {
            if (product.getProductName().equals(productDto.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if (isProductNotPresent) {

            Product product = modelMapper.map(productDto, Product.class);
            product.setCategory(category);

            product.setImage("default.png");

            Double price = product.getPrice();
            Double discount = product.getDiscount();

            if (price == null || price <= 0) {
                throw new ApiException("Product price must be greater than 0");
            }

            if (discount == null) {
                discount = 0.0;
            }

            if (discount <= 0 || discount > 100) {
                throw new ApiException("Product discount must be between 0 and 100");
            }

            Double specialPrice = price - ((discount * 0.01) * price);
            product.setSpecialPrice(specialPrice);

            Product savedProduct = productRepo.save(product);

            return modelMapper.map(savedProduct, ProductDto.class);
        } else {
            throw new ApiException("Product already exists");
        }


    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetail = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepo.findAll(pageDetail);
        List<Product> productList = productPage.getContent();
        if (productList.isEmpty()) throw new ResourceNotFoundException("No products found.");
        List<ProductDto> productDtoList = productList.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setPageDetail(paginationService.getPageInfo(productPage));
        productResponse.setContent(productDtoList);
        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetail = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepo.findByCategoryCategoryIdOrderByPriceAsc(categoryId, pageDetail);
        List<Product> productList = productPage.getContent();
        if (productList.isEmpty()) throw new ResourceNotFoundException("No products found.");

        List<ProductDto> productDtoList = productList.stream()
                .map(product -> modelMapper.map(product, ProductDto.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setPageDetail(paginationService.getPageInfo(productPage));
        productResponse.setContent(productDtoList);
        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetail = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepo.findByProductNameContainingIgnoreCase(keyword, pageDetail);
        List<Product> productList = productPage.getContent();
        if (productList.isEmpty()) throw new ResourceNotFoundException("No products found.");

        List<ProductDto> productDtoList = productList.stream()
                .map(product -> modelMapper.map(product, ProductDto.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setPageDetail(paginationService.getPageInfo(productPage));
        productResponse.setContent(productDtoList);
        return productResponse;
    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product productToUpdate = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        if (productDto.getProductName() != null) {
            productToUpdate.setProductName(productDto.getProductName());
        }
        if (productDto.getImage() != null) {
            productToUpdate.setImage("updated.png");
        }

        if (productDto.getPrice() != null) {
            productToUpdate.setPrice(productDto.getPrice());
        }
        if (productDto.getQuantity() != null) {
            productToUpdate.setQuantity(productDto.getQuantity());
        }
        if (productDto.getSpecialPrice() != null) {
            productToUpdate.setSpecialPrice(productDto.getSpecialPrice());
        }

        if (productDto.getDiscount() != null) {
            Double price = productToUpdate.getPrice();
            if (price == null) {
                throw new IllegalArgumentException("Price is required before applying discount");
            }
            Double discount = productDto.getDiscount();
            if (discount < 0 || discount > 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100");
            }
            Double specialPrice = price - ((discount * 0.01) * price);
            productToUpdate.setSpecialPrice(specialPrice);
            productToUpdate.setDiscount(productDto.getDiscount());
        }

        Product updatedProduct = productRepo.save(productToUpdate);

        ProductDto productDtoResponse = modelMapper.map(updatedProduct, ProductDto.class);
        productDtoResponse.setProductId(productId);
        return productDtoResponse;
    }

    @Override
    public ProductDto deleteProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));
        productRepo.delete(product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
        // get the product from the db

        Product productFromDb = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        // upload image to server
        // get the file name of uploaded image
        String fileName = fileService.uploadImage(path, image);
        // updating the new filename to the product
        productFromDb.setImage(fileName);
        // Save product to db
        Product updatedProduct = productRepo.save(productFromDb);
        // return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct, ProductDto.class);
    }


}
