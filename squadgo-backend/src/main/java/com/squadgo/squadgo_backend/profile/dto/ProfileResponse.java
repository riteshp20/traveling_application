package com.squadgo.squadgo_backend.profile.dto;

import com.squadgo.squadgo_backend.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private UUID userId;
    private String fullName;
    private String email;
    private String phone;
    private String bio;
    private List<String> interests;
    private String profilePhotoUrl;
    private Boolean isVerified;
    private Integer travelCoins;
    private Role role;
    private LocalDateTime createdAt;
}
