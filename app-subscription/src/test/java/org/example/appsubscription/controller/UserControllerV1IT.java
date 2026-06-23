package org.example.appsubscription.controller;

import jakarta.persistence.EntityManager;
import org.example.appsubscription.base.BaseContext;
import org.example.appsubscription.api.entity.SubscriptionType;
import org.example.appsubscription.api.entity.User;
import org.example.appsubscription.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserControllerV1IT extends BaseContext {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void findUser_WhenUserDoesNotExist_ShouldCreateFreeUserInPostgres() {
        String username = "integration_new_user";

        webTestClient.get()
                .uri("/api/v1/users/{user_name}", username)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(username)
                .jsonPath("$.subscriptionType").isEqualTo("FREE");

        User savedUser = userRepository.findById(username).orElse(null);
        assertNotNull(savedUser);
        assertEquals(SubscriptionType.FREE, savedUser.getSubscriptionType());
    }

    @Test
    void editTypeSubscription_NoShouldUpdateDatabase() {
        String username = "user_for_edit";

        webTestClient.patch()
                .uri("/api/v1/users/{userName}/type/{subscriptionType}", username, "PAID")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        User updatedUser = userRepository.findById(username).orElse(null);
        assertNull(updatedUser);
    }

    @Test
    void editTypeSubscription_ShouldUpdateDatabase() {
        String username = "user_for_edit";
        User userForEdit = new User();
        userForEdit.setName(username);
        userForEdit.setSubscriptionType(SubscriptionType.FREE);
        userRepository.save(userForEdit);

        webTestClient.patch()
                .uri("/api/v1/users/{userName}/type/{subscriptionType}", username, "PAID")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.subscriptionType").isEqualTo("PAID")
                .jsonPath("$.endDate").exists();

        User updatedUser = userRepository.findById(username).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(SubscriptionType.PAID, updatedUser.getSubscriptionType());
    }

    @Test
    void updateSubscription_WhenUserIsFree_ShouldReturn400BadRequest() {
        String username = "free_user_error";

        webTestClient.patch()
                .uri("/api/v1/users/{user_name}", username)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void updateSubscription_WhenUserIsPaid_ShouldExtendEndDateInDatabase() {
        String username = "paid_user_renew";
        User paidUserRenew = new User();
        paidUserRenew.setName(username);
        paidUserRenew.setSubscriptionType(SubscriptionType.PAID);
        LocalDate initialEndDate = LocalDate.now().plusDays(10);
        paidUserRenew.setEndDate(initialEndDate);
        userRepository.save(paidUserRenew);

        webTestClient.patch()
                .uri("/api/v1/users/{user_name}", username)
                .exchange()
                .expectStatus().isOk();

        User updatedUser = userRepository.findById(username).orElse(null);
        assertNotNull(updatedUser);
        assertTrue(updatedUser.getEndDate().isAfter(initialEndDate));
    }
}