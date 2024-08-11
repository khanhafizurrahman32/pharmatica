package org.example.pharmaticb.service.order;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
}
