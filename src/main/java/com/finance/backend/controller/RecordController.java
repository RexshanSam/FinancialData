package com.finance.backend.controller;

import com.finance.backend.dto.PagedResponse;
import com.finance.backend.dto.RecordRequest;
import com.finance.backend.dto.RecordResponse;
import com.finance.backend.enums.TransactionType;
import com.finance.backend.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @Operation(summary = "Create a new financial record (Admin/Analyst only)")
    @PostMapping
    public ResponseEntity<RecordResponse> createRecord(
            @Parameter(description = "Record request", required = true)
            @Valid @RequestBody RecordRequest request
    ) {
        System.out.println("Received request to create new record");
        String userEmail = getCurrentUserEmail();
        RecordResponse response = recordService.createRecord(request, userEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all financial records with optional filters")
    @GetMapping
    public ResponseEntity<PagedResponse<RecordResponse>> getAllRecords(
            @Parameter(description = "Transaction type filter") @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Category filter") @RequestParam(required = false) String category,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number (default: 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (default: 10)") @RequestParam(defaultValue = "10") int size
    ) {
        System.out.println("Fetching records with filters");
        PagedResponse<RecordResponse> response = recordService.getAllRecords(type, category, startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get record by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RecordResponse> getRecordById(
            @Parameter(description = "Record ID", required = true) @PathVariable Long id
    ) {
        System.out.println("Fetching record with ID: " + id);
        RecordResponse response = recordService.getRecordById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a financial record (Admin/Analyst only)")
    @PutMapping("/{id}")
    public ResponseEntity<RecordResponse> updateRecord(
            @Parameter(description = "Record ID", required = true) @PathVariable Long id,
            @Parameter(description = "Record request", required = true)
            @Valid @RequestBody RecordRequest request
    ) {
        System.out.println("Received request to update record with ID: " + id);
        String userEmail = getCurrentUserEmail();
        RecordResponse response = recordService.updateRecord(id, request, userEmail);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a financial record (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(
            @Parameter(description = "Record ID", required = true) @PathVariable Long id
    ) {
        System.out.println("Received request to delete record with ID: " + id);
        recordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserEmail() {
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
