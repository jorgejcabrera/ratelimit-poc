package domain.usecase

import domain.notification.NotificationService
import domain.ratelimit.RateLimitService

class SendNotificationUseCase(
    private val notificationService: NotificationService,
    private val rateLimitService: RateLimitService
) {

    fun invoke(type: String, userId: String, message: String) {
        rateLimitService.acquire(type, userId)
        notificationService.send(type, userId, message)
    }
}