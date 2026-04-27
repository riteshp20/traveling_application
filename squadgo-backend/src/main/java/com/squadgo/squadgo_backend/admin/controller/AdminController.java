package com.squadgo.squadgo_backend.admin.controller;

import com.squadgo.squadgo_backend.admin.dto.AdminStatsResponse;
import com.squadgo.squadgo_backend.admin.dto.AdminUserResponse;
import com.squadgo.squadgo_backend.admin.dto.UpdateUserRoleRequest;
import com.squadgo.squadgo_backend.admin.service.AdminService;
import com.squadgo.squadgo_backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // -------------------------------------------------------------------------
    // User listing & search
    // -------------------------------------------------------------------------

    /**
     * GET /api/v1/admin/users?page=0&size=20
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AdminUserResponse> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully.", users));
    }

    /**
     * GET /api/v1/admin/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(@PathVariable UUID userId) {
        AdminUserResponse user = adminService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully.", user));
    }

    /**
     * GET /api/v1/admin/users/search?q=john&page=0&size=20
     */
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> searchUsers(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AdminUserResponse> users = adminService.searchUsers(query, page, size);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched successfully.", users));
    }

    // -------------------------------------------------------------------------
    // User mutations
    // -------------------------------------------------------------------------

    /**
     * PUT /api/v1/admin/users/role
     * Body: { "userId": "...", "newRole": "PARTNER" }
     */
    @PutMapping("/users/role")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserRole(
            @Valid @RequestBody UpdateUserRoleRequest request) {

        AdminUserResponse updated = adminService.updateUserRole(request);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully.", updated));
    }

    /**
     * PUT /api/v1/admin/users/{userId}/verify
     */
    @PutMapping("/users/{userId}/verify")
    public ResponseEntity<ApiResponse<AdminUserResponse>> grantVerifiedBadge(@PathVariable UUID userId) {
        AdminUserResponse updated = adminService.grantVerifiedBadge(userId);
        return ResponseEntity.ok(ApiResponse.success("Verified badge granted.", updated));
    }

    /**
     * PUT /api/v1/admin/users/{userId}/unverify
     */
    @PutMapping("/users/{userId}/unverify")
    public ResponseEntity<ApiResponse<AdminUserResponse>> revokeVerifiedBadge(@PathVariable UUID userId) {
        AdminUserResponse updated = adminService.revokeVerifiedBadge(userId);
        return ResponseEntity.ok(ApiResponse.success("Verified badge revoked.", updated));
    }

    // -------------------------------------------------------------------------
    // Stats
    // -------------------------------------------------------------------------

    /**
     * GET /api/v1/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getSystemStats() {
        AdminStatsResponse stats = adminService.getSystemStats();
        return ResponseEntity.ok(ApiResponse.success("System stats fetched successfully.", stats));
    }
}
