package com.squadgo.squadgo_backend.admin.dto;

import com.squadgo.squadgo_backend.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {

    private UUID userId;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private Boolean isVerified;
    private Integer travelCoins;
    private LocalDateTime createdAt;
}
