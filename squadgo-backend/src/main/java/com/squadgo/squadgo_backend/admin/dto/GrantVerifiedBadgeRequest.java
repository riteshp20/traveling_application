package com.squadgo.squadgo_backend.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrantVerifiedBadgeRequest {

    @NotNull(message = "userId must not be null.")
    private UUID userId;
}
