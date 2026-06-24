package org.example.appsubscription.api.repository;

import org.example.appsubscription.api.entity.SubscriptionType;
import org.example.appsubscription.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    @Modifying(clearAutomatically = true)
    @Query(value = "update User u set u.subscriptionType = :toSubscriptionType where u.subscriptionType = :fromSubscriptionType and u.endDate < :curedDate")
    List<User> deactivateSubscription(SubscriptionType fromSubscriptionType,
                                      SubscriptionType toSubscriptionType,
                                      LocalDate curedDate);
}