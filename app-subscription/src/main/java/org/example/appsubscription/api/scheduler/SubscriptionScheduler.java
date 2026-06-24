package org.example.appsubscription.api.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.appsubscription.api.service.impl.UserServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final UserServiceImpl userService;

    @Scheduled(cron = "${server.scheduled.cronCheckSubscription}")
    public void invalidateExpiredSubscriptions() {
        userService.invalidateExpiredSubscriptions();
    }
}