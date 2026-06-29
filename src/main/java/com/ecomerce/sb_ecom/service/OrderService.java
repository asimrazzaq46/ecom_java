package com.ecomerce.sb_ecom.service;


import com.ecomerce.sb_ecom.exceptions.ApiException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.interfaces.ICartService;
import com.ecomerce.sb_ecom.interfaces.IOrderService;
import com.ecomerce.sb_ecom.model.*;
import com.ecomerce.sb_ecom.payload.order.OrderDto;
import com.ecomerce.sb_ecom.payload.order.OrderItemDto;
import com.ecomerce.sb_ecom.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private ICartRepository cartRepo;

    @Autowired
    private ICartService cartService;

    @Autowired
    private IAddressRepository addressRepo;

    @Autowired
    private IPaymentRepository paymentRepo;

    @Autowired
    private IOrderRepository orderRepo;

    @Autowired
    private IOrderItemRepository orderItemRepo;

    @Autowired
    private IProductRepository productRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        // Getting UserCart
        Cart cart = cartRepo.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("cart", "email", emailId);
        }

        Address address = addressRepo.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("address", "id", addressId));
        // create a new order with paymentInfo

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setStatus("Order Accepted !");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);

        payment = paymentRepo.save(payment);

        order.setPayment(payment);

        Order savedOrder = orderRepo.save(order);

        // get items from the cart into the orderItems

        List<CartItem> cartItemList = cart.getCartItems();
        if (cartItemList.isEmpty()) {
            throw new ApiException("Cart is empty");
        }

        List<OrderItem> orderItemList = new ArrayList<>();

        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItemList.add(orderItem);
        }

        orderItemList = orderItemRepo.saveAll(orderItemList);

        List<CartItem> itemsToProcess = new ArrayList<>(cart.getCartItems());

        // update product stock
        itemsToProcess.forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepo.save(product);
            // clear the cart
            cartService.deleteProductFromCart(cart.getCartId(), product.getId());

        });

        // send back the order summary

        OrderDto orderDto = modelMapper.map(savedOrder, OrderDto.class);
        orderDto.setOrderItems(new ArrayList<>());
        orderItemList.forEach(item -> orderDto.getOrderItems().add(modelMapper.map(item, OrderItemDto.class)));
        orderDto.setAddressId(addressId);
        return orderDto;
    }
}
