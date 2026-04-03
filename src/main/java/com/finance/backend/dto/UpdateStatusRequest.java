package com.finance.backend.dto;

import com.finance.backend.enums.Status;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {
    @NotNull(message = "Status is required")
    private Status status;

    public UpdateStatusRequest() {}

    public UpdateStatusRequest(Status status) {
        this.status = status;
    }

    // Getters and setters
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
