package com.finance.backend.dto;

import java.math.BigDecimal;

public class DashboardSummary {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private long totalRecords;

    public DashboardSummary() {}

    public DashboardSummary(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netBalance, long totalRecords) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netBalance = netBalance;
        this.totalRecords = totalRecords;
    }

    // Getters and setters
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }

    public BigDecimal getNetBalance() { return netBalance; }
    public void setNetBalance(BigDecimal netBalance) { this.netBalance = netBalance; }

    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
}
