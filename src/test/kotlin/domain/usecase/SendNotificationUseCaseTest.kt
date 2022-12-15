package domain.usecase

import domain.notification.NotificationService
import domain.ratelimit.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration.*

@ExtendWith(MockKExtension::class)
class SendNotificationUseCaseTest {

    @RelaxedMockK
    private lateinit var notificationService: NotificationService

    private lateinit var messageSentCounter: MessageSentCounter

    private lateinit var rateLimitService: RateLimitService

    private lateinit var sendNotificationUseCase: SendNotificationUseCase

    private val statusUnitOfTime = ofSeconds(1)

    @BeforeEach
    fun startUp() {
        val statusRateLimit = RateLimit("status", 2, statusUnitOfTime)
        val newsRateLimit = RateLimit(type = "news", 1, ofDays(1))

        messageSentCounter = MessageSentCounterImpl(
            listOf(
                Cache(statusRateLimit.type, statusRateLimit.unitOfTime),
                Cache(newsRateLimit.type, newsRateLimit.unitOfTime),
            )
        )
        rateLimitService = RateLimitServiceImpl(messageSentCounter, rateLimits = listOf(statusRateLimit, newsRateLimit))
        sendNotificationUseCase = SendNotificationUseCase(notificationService, rateLimitService)
    }

    @Test
    fun `when the quote has not been exceeded, then the notification must be dispatched`() {
        // WHEN
        sendNotificationUseCase.invoke("status", "cabrerajjorge@gmail.com", "Hi Jorge!")

        // THEN
        verify(exactly = 1) { notificationService.send(any(), any(), any()) }
    }

    @Test
    fun `when the quote of a news has not been exceeded, then the notification must be dispatched`() {
        // WHEN
        sendNotificationUseCase.invoke("news", "cabrerajjorge@gmail.com", "Hi Jorge!")

        // THEN
        verify(exactly = 1) { notificationService.send(any(), any(), any()) }
    }

    @Test
    fun `when the quote has been exceeded, then an exception must be thrown`() {
        // WHEN
        val exception = assertThrows(RuntimeException::class.java) {
            repeat(3) {
                sendNotificationUseCase.invoke("status", "cabrerajjorge@gmail.com", "Hi Jorge!")
            }
        }

        // THEN
        assertEquals("Maximum number of request has been reached", exception.message)
    }

    @Test
    fun `when the quote has reached the maximum threshold, then non exception must be thrown`() {
        // WHEN
        repeat(2) {
            sendNotificationUseCase.invoke("status", "cabrerajjorge@gmail.com", "Hi Jorge!")
        }

        // THEN
        noExceptionWasThrown()
    }

    @Test
    fun `a request must be expired properly`() {
        // WHEN
        repeat(3) {
            sendNotificationUseCase.invoke("status", "cabrerajjorge@gmail.com", "Hi Jorge!")
            Thread.sleep(statusUnitOfTime.toMillis() / 2)
        }

        // THEN
        noExceptionWasThrown()
    }

    private fun noExceptionWasThrown() {

    }
}