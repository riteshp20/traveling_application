package com.squadgo.squadgo_backend.auth.dto;

import com.squadgo.squadgo_backend.auth.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private UUID userId;
    private String fullName;
    private String email;
    private Role role;
    private Boolean isVerified;
    private Integer travelCoins;
    private String accessToken;
    private String tokenType;
}
