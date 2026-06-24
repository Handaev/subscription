package org.example.appsubscription.api.entity;

import java.util.Arrays;

public enum SubscriptionType {
    PAID,
    FREE;

    public static SubscriptionType getEnumType(String subscriptionType) {
        return Arrays.stream(SubscriptionType.values())
                .filter(type -> type.name().equalsIgnoreCase(subscriptionType))
                .findFirst()
                .orElse(null);
    }
}