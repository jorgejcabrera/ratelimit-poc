package domain.ratelimit

import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration

class Cache(
    val keyPrefix: String,
    timeToLive: Duration,
    maximumEntries: Long = Long.MAX_VALUE,
) {
    private val caffeineCache = Caffeine.newBuilder()
        .maximumSize(maximumEntries)
        .expireAfterWrite(timeToLive)
        .recordStats()
        .build<String, Int>()

    private fun buildKey(userId: String): String {
        return "$keyPrefix-$userId"
    }

    fun save(userId: String) {
        val key = buildKey(userId)
        val cachedElement = caffeineCache.getIfPresent(key)
        if (cachedElement != null) {
            caffeineCache.put(key, cachedElement + 1)
        } else {
            caffeineCache.put(key, 1)
        }
    }

    fun get(userId: String): Int {
        val key = buildKey(userId)
        return caffeineCache.getIfPresent(key) ?: 0
    }
}