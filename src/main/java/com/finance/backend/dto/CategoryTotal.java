package com.finance.backend.dto;

import java.math.BigDecimal;

public class CategoryTotal {
    private String category;
    private BigDecimal total;

    public CategoryTotal() {}

    public CategoryTotal(String category, BigDecimal total) {
        this.category = category;
        this.total = total;
    }

    // Getters and setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
