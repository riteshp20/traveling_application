package com.squadgo.squadgo_backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {

    private Long totalUsers;
    private Long verifiedUsers;
    private Long totalDestinations;
    private Long activeDestinations;

    /** Placeholder — requires BookingRepository when implemented. */
    private Long totalBookings;

    /** Placeholder — requires ReviewRepository when implemented. */
    private Long totalReviews;
}
