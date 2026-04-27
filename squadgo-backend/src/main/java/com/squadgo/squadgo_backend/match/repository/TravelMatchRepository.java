package com.squadgo.squadgo_backend.match.repository;

import com.squadgo.squadgo_backend.match.entity.MatchStatus;
import com.squadgo.squadgo_backend.match.entity.TravelMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TravelMatchRepository extends JpaRepository<TravelMatch, UUID> {

    Optional<TravelMatch> findByRequesterIdAndReceiverId(UUID requesterId, UUID receiverId);

    List<TravelMatch> findByReceiverIdAndStatus(UUID receiverId, MatchStatus status);

    /**
     * Returns all matches where the user is either requester or receiver with the given status.
     * Spring Data's generated query for this method name would be ambiguous, so we use a custom JPQL query.
     */
    @Query("SELECT tm FROM TravelMatch tm WHERE (tm.requester.id = :requesterId OR tm.receiver.id = :receiverId) AND tm.status = :status")
    List<TravelMatch> findByRequesterIdOrReceiverIdAndStatus(
            @Param("requesterId") UUID requesterId,
            @Param("receiverId") UUID receiverId,
            @Param("status") MatchStatus status
    );

    boolean existsByRequesterIdAndReceiverId(UUID requesterId, UUID receiverId);

    List<TravelMatch> findByRequesterIdAndStatus(UUID requesterId, MatchStatus status);
}
