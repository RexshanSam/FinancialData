package com.finance.backend.service;

import com.finance.backend.dto.PagedResponse;
import com.finance.backend.dto.RecordRequest;
import com.finance.backend.dto.RecordResponse;
import com.finance.backend.enums.TransactionType;
import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.model.FinancialRecord;
import com.finance.backend.model.User;
import com.finance.backend.repository.RecordRepository;
import com.finance.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    public RecordService(RecordRepository recordRepository, UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public RecordResponse createRecord(RecordRequest request, String userEmail) {
        System.out.println("Creating new record for user: " + userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());
        record.setIsDeleted(false);
        record.setCreatedBy(user);

        record = recordRepository.save(record);
        System.out.println("Record created successfully with ID: " + record.getId());

        return convertToResponse(record);
    }

    public PagedResponse<RecordResponse> getAllRecords(
            TransactionType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {
        System.out.println("Fetching records with filters - type: " + type + ", category: " + category +
                ", startDate: " + startDate + ", endDate: " + endDate + ", page: " + page + ", size: " + size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<FinancialRecord> recordPage = recordRepository.findAllWithFilters(type, category, startDate, endDate, pageable);

        List<RecordResponse> content = new ArrayList<>();
        for (FinancialRecord record : recordPage.getContent()) {
            content.add(convertToResponse(record));
        }

        PagedResponse<RecordResponse> response = new PagedResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(recordPage.getTotalElements());
        response.setTotalPages(recordPage.getTotalPages());
        response.setLast(recordPage.isLast());
        return response;
    }

    public RecordResponse getRecordById(Long id) {
        System.out.println("Fetching record with ID: " + id);
        FinancialRecord record = recordRepository.findById(id)
                .filter(r -> !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        return convertToResponse(record);
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public RecordResponse updateRecord(Long id, RecordRequest request, String userEmail) {
        System.out.println("Updating record with ID: " + id + " for user: " + userEmail);

        FinancialRecord record = recordRepository.findById(id)
                .filter(r -> !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());

        record = recordRepository.save(record);
        System.out.println("Record updated successfully with ID: " + record.getId());

        return convertToResponse(record);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRecord(Long id) {
        System.out.println("Deleting record with ID: " + id);
        FinancialRecord record = recordRepository.findById(id)
                .filter(r -> !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        record.setIsDeleted(true);
        recordRepository.save(record);
        System.out.println("Record deleted (soft delete) with ID: " + id);
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
