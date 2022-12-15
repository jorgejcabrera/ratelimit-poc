package domain.ratelimit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime

class CacheTest {
    @Test
    fun `when some items have expired, then it must not be retrieved`() {
        // GIVEN
        val cache = Cache(
            keyPrefix = "status",
            timeToLive = Duration.ofSeconds(5)
        )
        givenAnExpiredItemSavedIn(cache)

        // WHEN
        cache.save("jorge")

        // THEN
        thenOnlyOneItemHasNotExpired(cache)
    }

    @Test
    fun `when the items stored have expired, then the counter must retrieved 0`() {
        // GIVEN
        val cache = Cache(
            keyPrefix = "status",
            timeToLive = Duration.ofNanos(1)
        )

        // WHEN
        cache.save("jorge")
        cache.save("jorge")
        cache.save("jorge")

        // THEN
        Assertions.assertEquals(0, cache.counter("jorge"))
    }

    private fun thenOnlyOneItemHasNotExpired(cache: Cache) {
        Assertions.assertEquals(1, cache.counter("jorge"))
    }

    private fun givenAnExpiredItemSavedIn(cache: Cache) {
        cache.caffeineCache.put(
            "status-jorge",
            mutableListOf(Item(expiredAt = LocalDateTime.now().minus(Duration.ofSeconds(5))))
        )
    }

}