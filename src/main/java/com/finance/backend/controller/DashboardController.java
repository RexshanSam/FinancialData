package com.finance.backend.controller;

import com.finance.backend.dto.CategoryTotal;
import com.finance.backend.dto.DashboardSummary;
import com.finance.backend.dto.MonthlyTrend;
import com.finance.backend.dto.RecordResponse;
import com.finance.backend.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Get dashboard summary (Admin/Analyst only)")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary() {
        System.out.println("Fetching dashboard summary");
        DashboardSummary summary = dashboardService.getSummary();
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Get category totals (Admin/Analyst only)")
    @GetMapping("/by-category")
    public ResponseEntity<List<CategoryTotal>> getCategoryTotals() {
        System.out.println("Fetching category totals");
        List<CategoryTotal> totals = dashboardService.getCategoryTotals();
        return ResponseEntity.ok(totals);
    }

    @Operation(summary = "Get monthly trends (Admin/Analyst only)")
    @GetMapping("/monthly-trends")
    public ResponseEntity<List<MonthlyTrend>> getMonthlyTrends() {
        System.out.println("Fetching monthly trends");
        List<MonthlyTrend> trends = dashboardService.getMonthlyTrends();
        return ResponseEntity.ok(trends);
    }

    @Operation(summary = "Get recent transactions (Admin/Analyst only)")
    @GetMapping("/recent")
    public ResponseEntity<List<RecordResponse>> getRecentTransactions() {
        System.out.println("Fetching recent transactions");
        List<RecordResponse> transactions = dashboardService.getRecentTransactions();
        return ResponseEntity.ok(transactions);
    }
}
