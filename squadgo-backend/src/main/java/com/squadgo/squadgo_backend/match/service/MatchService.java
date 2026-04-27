package com.squadgo.squadgo_backend.match.service;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.auth.repository.UserRepository;
import com.squadgo.squadgo_backend.checkin.entity.CheckIn;
import com.squadgo.squadgo_backend.checkin.repository.CheckInRepository;
import com.squadgo.squadgo_backend.destination.entity.Destination;
import com.squadgo.squadgo_backend.destination.repository.DestinationRepository;
import com.squadgo.squadgo_backend.match.dto.MatchResponse;
import com.squadgo.squadgo_backend.match.dto.NearbyTravelerResponse;
import com.squadgo.squadgo_backend.match.dto.SendMatchRequest;
import com.squadgo.squadgo_backend.match.entity.MatchStatus;
import com.squadgo.squadgo_backend.match.entity.TravelMatch;
import com.squadgo.squadgo_backend.match.repository.TravelMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final TravelMatchRepository travelMatchRepository;
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    @Transactional(readOnly = true)
    public List<NearbyTravelerResponse> findNearbyTravelers(String email) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CheckIn myCheckIn = checkInRepository.findByUserIdAndIsActiveTrue(currentUser.getId())
                .orElseThrow(() -> new RuntimeException(
                        "You must be checked in at a destination to see nearby travelers."
                ));

        UUID destinationId = myCheckIn.getDestination().getId();

        return checkInRepository.findByDestinationIdAndIsActiveTrue(destinationId)
                .stream()
                .filter(checkIn -> !checkIn.getUser().getId().equals(currentUser.getId()))
                .map(checkIn -> {
                    User traveler = checkIn.getUser();
                    boolean alreadyRequested = travelMatchRepository
                            .existsByRequesterIdAndReceiverId(currentUser.getId(), traveler.getId());

                    List<String> interests = parseInterests(traveler.getInterests());

                    return NearbyTravelerResponse.builder()
                            .userId(traveler.getId())
                            .fullName(traveler.getFullName())
                            .profilePhotoUrl(traveler.getProfilePhotoUrl())
                            .isVerified(traveler.getIsVerified())
                            .interests(interests)
                            .destinationId(checkIn.getDestination().getId())
                            .destinationName(checkIn.getDestination().getName())
                            .checkedInAt(checkIn.getCheckedInAt())
                            .alreadyRequested(alreadyRequested)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchResponse sendMatchRequest(String email, SendMatchRequest request) {
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Requester must be checked in at that destination
        CheckIn requesterCheckIn = checkInRepository.findByUserIdAndIsActiveTrue(requester.getId())
                .orElseThrow(() -> new RuntimeException(
                        "You must be checked in at the destination to send a match request."
                ));

        if (!requesterCheckIn.getDestination().getId().equals(request.getDestinationId())) {
            throw new RuntimeException(
                    "You are not checked in at the specified destination."
            );
        }

        // Receiver must be checked in at the same destination
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        CheckIn receiverCheckIn = checkInRepository.findByUserIdAndIsActiveTrue(receiver.getId())
                .orElseThrow(() -> new RuntimeException(
                        "The traveler you want to connect with is not currently checked in at this destination."
                ));

        if (!receiverCheckIn.getDestination().getId().equals(request.getDestinationId())) {
            throw new RuntimeException(
                    "The receiver is not checked in at the specified destination."
            );
        }

        // Prevent duplicate requests
        if (travelMatchRepository.existsByRequesterIdAndReceiverId(requester.getId(), receiver.getId())) {
            throw new RuntimeException("You have already sent a match request to this traveler.");
        }

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        TravelMatch travelMatch = TravelMatch.builder()
                .requester(requester)
                .receiver(receiver)
                .destination(destination)
                .status(MatchStatus.PENDING)
                .message(request.getMessage())
                .build();

        TravelMatch saved = travelMatchRepository.save(travelMatch);
        return toResponse(saved);
    }

    @Transactional
    public MatchResponse respondToMatchRequest(String email, UUID matchId, boolean accept) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TravelMatch travelMatch = travelMatchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match request not found"));

        if (!travelMatch.getReceiver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to respond to this match request.");
        }

        if (travelMatch.getStatus() != MatchStatus.PENDING) {
            throw new RuntimeException("This match request has already been responded to.");
        }

        travelMatch.setStatus(accept ? MatchStatus.ACCEPTED : MatchStatus.DECLINED);

        TravelMatch saved = travelMatchRepository.save(travelMatch);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getIncomingRequests(String email) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return travelMatchRepository
                .findByReceiverIdAndStatus(currentUser.getId(), MatchStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMyConnections(String email) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return travelMatchRepository
                .findByRequesterIdOrReceiverIdAndStatus(
                        currentUser.getId(), currentUser.getId(), MatchStatus.ACCEPTED
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // --- Helpers ---

    private MatchResponse toResponse(TravelMatch match) {
        return MatchResponse.builder()
                .matchId(match.getId())
                .requesterId(match.getRequester().getId())
                .requesterName(match.getRequester().getFullName())
                .requesterPhotoUrl(match.getRequester().getProfilePhotoUrl())
                .receiverId(match.getReceiver().getId())
                .receiverName(match.getReceiver().getFullName())
                .receiverPhotoUrl(match.getReceiver().getProfilePhotoUrl())
                .destinationId(match.getDestination().getId())
                .destinationName(match.getDestination().getName())
                .status(match.getStatus())
                .message(match.getMessage())
                .createdAt(match.getCreatedAt())
                .build();
    }

    private List<String> parseInterests(String interests) {
        if (interests == null || interests.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(interests.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
