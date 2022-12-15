package domain.ratelimit

import java.lang.RuntimeException

interface RateLimitService {
    fun acquire(type: String, userId: String)
}

class RateLimitServiceImpl(
    private val messageSentCounter: MessageSentCounter,
    private val rateLimits: List<RateLimit>
) : RateLimitService {
    override fun acquire(type: String, userId: String) {
        val rateLimit = rateLimits.find { it.type == type }
        val quote = messageSentCounter.quote(type, userId)
        if (quoteHasBeenExceeded(quote, rateLimit!!)) {
            throw RuntimeException("Maximum number of request has been reached")
        }
        messageSentCounter.increase(type, userId)
    }

    private fun quoteHasBeenExceeded(quote: Int, rateLimit: RateLimit): Boolean {
        return quote >= rateLimit.threshold
    }

}
