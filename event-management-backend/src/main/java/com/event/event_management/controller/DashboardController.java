package com.event.event_management.controller;

import com.event.event_management.dto.DashboardResponse;
import com.event.event_management.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dashboard APIs")
@RestController
@RequestMapping("/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(
            summary = "Get Dashboard Statistics",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public DashboardResponse getDashboard() {
        return dashboardService.getDashboard();
    }
}