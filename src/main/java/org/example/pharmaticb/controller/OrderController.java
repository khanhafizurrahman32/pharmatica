package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.service.order.OrderService;
import org.springframework.web.bind.annotation.RestController;

@BaseController
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
}
