package com.brokerage.controller;

import com.brokerage.entity.Order;
import com.brokerage.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/matchOrders")
    @Operation(summary = "Match orders", description = "Match and return all pending orders by updating related assets")
    public List<Order> matchPendingOrders() {
        return adminService.matchPendingOrders();
    }
}
