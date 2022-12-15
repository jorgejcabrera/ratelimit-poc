package delivery

import delivery.config.ApplicationHealthCheck
import delivery.config.ServiceConfiguration
import delivery.rest.NotificationController
import domain.notification.NotificationServiceImpl
import domain.ratelimit.Cache
import domain.ratelimit.MessageSentCounterImpl
import domain.ratelimit.RateLimitServiceImpl
import domain.ratelimit.RateLimit
import domain.usecase.SendNotificationUseCase
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import java.time.Duration


class ServiceApp : Application<ServiceConfiguration>() {

    override fun run(configuration: ServiceConfiguration, environment: Environment) {
        val defaultSize: Int = configuration.defaultSize

        val statusRateLimit = RateLimit(type = "status", 2, Duration.ofMinutes(1))
        val newsRateLimit = RateLimit(type = "news", 1, Duration.ofDays(1))
        val marketingRateLimit = RateLimit(type = "marketing", 3, Duration.ofHours(1))
        val messageCounter = MessageSentCounterImpl(
            listOf(
                Cache(
                    keyPrefix = statusRateLimit.type,
                    timeToLive = statusRateLimit.unitOfTime,
                    maximumEntries = 10000
                ),
                Cache(
                    keyPrefix = newsRateLimit.type,
                    timeToLive = newsRateLimit.unitOfTime,
                    maximumEntries = 10000
                ),
                Cache(
                    keyPrefix = marketingRateLimit.type,
                    timeToLive = marketingRateLimit.unitOfTime,
                    maximumEntries = 10000
                )
            )
        )

        val rateLimits = listOf(
            statusRateLimit,
            newsRateLimit,
            marketingRateLimit
        )
        val rateLimitService = RateLimitServiceImpl(
            messageSentCounter = messageCounter,
            rateLimits = rateLimits
        )
        val notificationService = NotificationServiceImpl()
        val notificationUseCase = SendNotificationUseCase(notificationService, rateLimitService)

        environment
            .jersey()
            .register(NotificationController(notificationUseCase))

        environment
            .healthChecks()
            .register("domain/ratelimit", ApplicationHealthCheck())
    }
}