package domain.ratelimit

import java.time.Duration

data class RateLimit(
    val type: String,
    val threshold: Int,
    val unitOfTime: Duration
)