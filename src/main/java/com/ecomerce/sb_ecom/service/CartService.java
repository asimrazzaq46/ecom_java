package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.ApiException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.interfaces.ICartService;
import com.ecomerce.sb_ecom.interfaces.IPaginationService;
import com.ecomerce.sb_ecom.model.Cart;
import com.ecomerce.sb_ecom.model.CartItem;
import com.ecomerce.sb_ecom.model.Product;
import com.ecomerce.sb_ecom.payload.cart.CartDto;
import com.ecomerce.sb_ecom.payload.product.ProductDto;
import com.ecomerce.sb_ecom.repositories.ICartItemRepository;
import com.ecomerce.sb_ecom.repositories.ICartRepository;
import com.ecomerce.sb_ecom.repositories.IProductRepository;
import com.ecomerce.sb_ecom.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartService implements ICartService {

    @Autowired
    private ICartRepository cartRepo;
    @Autowired
    private ICartItemRepository cartItemRepo;
    @Autowired
    private IProductRepository productRepo;
    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IPaginationService paginationService;

    @Override
    public CartDto addProductToCart(Long productId, Integer quantity) {
        // Find existing cart or create one

        Cart cart = createCart();

        // Retrieve product details
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "ProductId", productId));

        // perform validations
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem != null) {
            throw new ApiException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new ApiException("Product " + product.getProductName() + " doesn't have enough stock");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("Please make an order of the " + product.getProductName() +
                    " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        // create cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setCart(cart);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // save cart item
        cartItemRepo.save(newCartItem);

//        product.setQuantity(product.getQuantity() - quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepo.save(cart);
        // return updated cart

        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        List<CartItem> cartItemList = cart.getCartItems();
        Stream<ProductDto> productDtoStream = cartItemList.stream().map(item -> {
            ProductDto prod = modelMapper.map(item.getProduct(), ProductDto.class);
            prod.setQuantity(item.getQuantity());
            return prod;
        });

        cartDto.setProducts(productDtoStream.toList());


        return cartDto;
    }
//
//    @Override
//    public List<CartDto> getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
//        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
//                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        Pageable pageDetails = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
//        Page<Cart> cartPage = cartRepo.findAll(pageDetails);
//        List<Cart> cartList = cartPage.getContent();
//        if (cartList.isEmpty()) throw new ResourceNotFoundException("No cart found.");
//
//        return cartList.stream().map(this::mapCartToCartDto).toList();
//    }

    @Override
    public List<CartDto> getAllCarts() {
        List<Cart> cartList = cartRepo.findAll();
        if (cartList.isEmpty()) throw new ResourceNotFoundException("No cart found.");

        return cartList.stream().map(this::mapCartToCartDto).toList();
    }


    private Cart createCart() {
        Cart userCart = cartRepo.findCartByEmail(authUtils.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtils.loggedInUser());
        return cartRepo.save(cart);
    }


    private CartDto mapCartToCartDto(Cart cart) {
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        List<ProductDto> products = cart.getCartItems().stream().map(c -> modelMapper.map(c, ProductDto.class)).toList();
        cartDto.setProducts(products);
        return cartDto;
    }
}
