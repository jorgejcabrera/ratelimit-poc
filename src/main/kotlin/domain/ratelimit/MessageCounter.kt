package domain.ratelimit

interface MessageCounter {
    fun quote(type: String, userId: String): Int
    fun increase(type: String, userId: String)
}

class MessageCounterImpl(private val buckets: List<Cache>) : MessageCounter {
    override fun quote(type: String, userId: String): Int {
        return buckets.find { it.keyPrefix == type }?.counter(userId) ?: 0
    }

    override fun increase(type: String, userId: String) {
        buckets.find { it.keyPrefix == type }?.save(userId)
    }
}