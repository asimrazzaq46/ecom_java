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
import jakarta.transaction.Transactional;
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

    @Override
    public CartDto getCartByEmailAndId(String emailId, Long cartId) {
        Cart cart = cartRepo.findCartByEmailAndCartId(emailId, cartId);

        if (cart == null) throw new ResourceNotFoundException("cart not found.");
        return mapCartToCartDto(cart);
    }

    @Transactional
    @Override
    public CartDto updateCartProductQuantity(Long productId, int delete) {

        Cart cart = cartRepo.findCartByEmail(authUtils.loggedInEmail());
        if (cart == null) throw new ResourceNotFoundException("cart not found.");
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product", "ProductId", productId));
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("product", "ProductId", productId);
        }
        int updatedQuantity = cartItem.getQuantity() + delete;

        if (updatedQuantity <= 0) {
            cart.getCartItems().removeIf(item -> item.getId().equals(cartItem.getId()));
            cartItem.setCart(null);
            cartItemRepo.delete(cartItem);
        } else {
            if (updatedQuantity > product.getQuantity()) {
                throw new ApiException("Product " + product.getProductName() + " doesn't have enough stock");
            }
            cartItem.setQuantity(updatedQuantity);
            cartItemRepo.save(cartItem);
        }

        double totalPrice = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();

        cart.setTotalPrice(totalPrice);
        Cart updatedCart = cartRepo.save(cart);
        return mapCartToCartDto(updatedCart);

    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("cart not found."));
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("product", "ProductId", productId);
        }
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItem.getId()));
        cartItem.setCart(null);
        cartItemRepo.delete(cartItem);
        cartItemRepo.flush();

        double totalPrice = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();

        cart.setTotalPrice(totalPrice);
        cartRepo.save(cart);

        return "product " + cartItem.getProduct().getProductName() + " has been deleted";
    }

    @Override
    public void UpdateProductInCarts(Long id, Long productId) {
        Cart cart = cartRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cart not found."));
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "ProductId", productId));

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("product", "ProductId", productId);
        }
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepo.save(cartItem);

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
        List<ProductDto> products = cart.getCartItems()
                .stream()
                .map(this::mapCartItemToProductDto)
                .toList();
        cartDto.setProducts(products);
        return cartDto;
    }

    private ProductDto mapCartItemToProductDto(CartItem item) {

        ProductDto productDto = modelMapper.map(item.getProduct(), ProductDto.class);

        productDto.setQuantity(item.getQuantity());

        return productDto;
    }

    private double calculateCartItemTotal(CartItem item) {

        double productPrice = item.getProductPrice() == null ? 0.0 : item.getProductPrice();

        int quantity = item.getQuantity() == null ? 0 : item.getQuantity();

        return productPrice * quantity;
    }


}
