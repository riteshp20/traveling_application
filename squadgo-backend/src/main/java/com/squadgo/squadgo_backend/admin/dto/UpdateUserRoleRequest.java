package com.squadgo.squadgo_backend.admin.dto;

import com.squadgo.squadgo_backend.auth.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRoleRequest {

    @NotNull(message = "userId must not be null.")
    private UUID userId;

    @NotNull(message = "newRole must not be null.")
    private Role newRole;
}
