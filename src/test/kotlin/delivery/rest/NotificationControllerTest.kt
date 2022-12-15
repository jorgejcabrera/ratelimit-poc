package delivery.rest

import domain.notification.NotificationService
import domain.ratelimit.*
import domain.usecase.SendNotificationUseCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(MockKExtension::class)
class NotificationControllerTest {

    @RelaxedMockK
    private lateinit var notificationService: NotificationService

    private lateinit var controller: NotificationController

    private lateinit var sendNotificationUseCase: SendNotificationUseCase

    private lateinit var messageCounter: MessageCounter

    private lateinit var rateLimitService: RateLimitService

    @BeforeEach
    fun setUp() {
        val newsRateLimit = RateLimit(type = "news", 1, Duration.ofSeconds(1))
        messageCounter = MessageCounterImpl(listOf(Cache(newsRateLimit.type, newsRateLimit.unitOfTime)))
        rateLimitService = RateLimitServiceImpl(messageCounter, rateLimits = listOf(newsRateLimit))
        sendNotificationUseCase = SendNotificationUseCase(notificationService, rateLimitService)
        controller = NotificationController(sendNotificationUseCase)
    }

    @Test
    fun `when the maximum request amount has been reached, then the an exception must be thrown`() {
        // GIVEN
        val request = SendNotificationRequest(userId = "cabrerajjorge@gmail.com", type = "news", message = "Hi Jorge!")

        // WHEN
        assertThrows(RuntimeException::class.java) {
            repeat(2) {
                controller.dispatch(request)
            }
        }
    }

    @Test
    fun `when the maximum request amount has not been reached, then an accepted must be returned`() {
        // GIVEN
        val request = SendNotificationRequest(userId = "cabrerajjorge@gmail.com", type = "news", message = "Hi Jorge!")

        // WHEN
        val response = controller.dispatch(request)

        // THEN
        assertEquals(HttpStatus.ACCEPTED_202, response.status)
    }
}