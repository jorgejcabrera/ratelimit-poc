package domain.ratelimit

import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import java.time.LocalDateTime

data class Item(val expiredAt: LocalDateTime)
class Cache(
    val keyPrefix: String,
    val timeToLive: Duration,
    maximumEntries: Long = Long.MAX_VALUE,
) {
    internal val caffeineCache = Caffeine.newBuilder()
        .maximumSize(maximumEntries)
        .expireAfterWrite(timeToLive)
        .recordStats()
        .build<String, MutableList<Item>>()

    private fun buildKey(userId: String): String {
        return "$keyPrefix-$userId"
    }

    fun save(userId: String) {
        val key = buildKey(userId)
        val cachedElement = caffeineCache.getIfPresent(key)
        if (cachedElement != null) {
            cachedElement.add(Item(LocalDateTime.now().plus(timeToLive)))
            caffeineCache.put(key, cachedElement)
        } else {
            caffeineCache.put(key, mutableListOf(Item(LocalDateTime.now().plus(timeToLive))))
        }
    }

    fun counter(userId: String): Int {
        val key = buildKey(userId)
        val items = caffeineCache.getIfPresent(key)
        return items?.filter { it.expiredAt >= LocalDateTime.now() }?.size ?: 0
    }
}