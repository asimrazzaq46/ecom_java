package com.ecomerce.sb_ecom.interfaces;

import com.ecomerce.sb_ecom.payload.order.OrderDto;
import jakarta.transaction.Transactional;

public interface IOrderService {
    @Transactional
    OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
