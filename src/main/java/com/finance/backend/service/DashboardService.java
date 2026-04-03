package com.finance.backend.service;

import com.finance.backend.dto.CategoryTotal;
import com.finance.backend.dto.DashboardSummary;
import com.finance.backend.dto.MonthlyTrend;
import com.finance.backend.dto.RecordResponse;
import com.finance.backend.enums.TransactionType;
import com.finance.backend.model.FinancialRecord;
import com.finance.backend.model.User;
import com.finance.backend.repository.RecordRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
public class DashboardService {

    private final RecordRepository recordRepository;

    public DashboardService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public DashboardSummary getSummary() {
        System.out.println("Generating dashboard summary");

        BigDecimal totalIncome = recordRepository.findTotalByType(TransactionType.INCOME);
        BigDecimal totalExpense = recordRepository.findTotalByType(TransactionType.EXPENSE);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);
        long totalRecords = recordRepository.countByIsDeletedFalse();

        DashboardSummary summary = new DashboardSummary();
        summary.setTotalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO);
        summary.setTotalExpense(totalExpense != null ? totalExpense : BigDecimal.ZERO);
        summary.setNetBalance(netBalance);
        summary.setTotalRecords(totalRecords);
        return summary;
    }

    public List<CategoryTotal> getCategoryTotals() {
        System.out.println("Fetching category totals");
        List<Object[]> results = recordRepository.findTotalGroupedByCategory();
        List<CategoryTotal> totals = new ArrayList<>();

        for (Object[] row : results) {
            CategoryTotal total = new CategoryTotal();
            total.setCategory((String) row[0]);
            total.setTotal((BigDecimal) row[1]);
            totals.add(total);
        }
        return totals;
    }

    public List<MonthlyTrend> getMonthlyTrends() {
        System.out.println("Fetching monthly trends");
        List<Object[]> results = recordRepository.findMonthlyTrends();
        List<MonthlyTrend> trends = new ArrayList<>();

        for (Object[] row : results) {
            MonthlyTrend trend = new MonthlyTrend();
            trend.setMonth((String) row[0]);
            trend.setIncome((BigDecimal) row[1]);
            trend.setExpense((BigDecimal) row[2]);
            trends.add(trend);
        }
        return trends;
    }

    public List<RecordResponse> getRecentTransactions() {
        System.out.println("Fetching recent transactions");
        List<FinancialRecord> records = recordRepository.findTop10ByIsDeletedFalseOrderByDateDesc();
        List<RecordResponse> responses = new ArrayList<>();

        for (FinancialRecord record : records) {
            responses.add(convertToResponse(record));
        }
        return responses;
    }

    private RecordResponse convertToResponse(FinancialRecord record) {
        User creator = record.getCreatedBy();
        RecordResponse response = new RecordResponse();
        response.setId(record.getId());
        response.setAmount(record.getAmount());
        response.setType(record.getType());
        response.setCategory(record.getCategory());
        response.setDate(record.getDate());
        response.setNotes(record.getNotes());
        response.setCreatedByName(creator != null ? creator.getName() : null);
        response.setCreatedByEmail(creator != null ? creator.getEmail() : null);
        return response;
    }
}
