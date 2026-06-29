package com.ecomerce.sb_ecom.controller;

import com.ecomerce.sb_ecom.interfaces.IOrderService;
import com.ecomerce.sb_ecom.payload.order.OrderDto;
import com.ecomerce.sb_ecom.payload.order.OrderRequestDto;
import com.ecomerce.sb_ecom.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDto> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDto orderRequestDto) {
        String emailId = authUtils.loggedInEmail();
        OrderDto orderDto = orderService.placeOrder(emailId, orderRequestDto.getAddressId(),
                paymentMethod, orderRequestDto.getPgName(), orderRequestDto.getPgPaymentId(),
                orderRequestDto.getPgStatus(), orderRequestDto.getPgResponseMessage());

        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }
}
