package com.fooddelivery.enums;

import java.util.Collections;
import java.util.List;

public enum OrderStatus {
    PENDING,
    ACCEPTED,
    PREPARING,
    OUT_FOR_DELIVERY,
    ARRIVING,
    DELIVERED,
    CANCELLED;

    /**
     * Returns the list of valid next statuses from this state.
     * This is the single source of truth for the state machine —
     * it is exposed via OrderResponseDto so the frontend never
     * needs its own transition map.
     */
    public List<String> getAllowedTransitions() {
        return switch (this) {
            case PENDING          -> List.of("ACCEPTED", "CANCELLED");
            case ACCEPTED         -> List.of("PREPARING", "CANCELLED");
            case PREPARING        -> List.of("OUT_FOR_DELIVERY");
            case OUT_FOR_DELIVERY -> List.of("ARRIVING");
            case ARRIVING         -> List.of("DELIVERED");
            case DELIVERED        -> Collections.emptyList();
            case CANCELLED        -> Collections.emptyList();
        };
    }
}
