package domain.ratelimit

interface MessageSentCounter {
    fun quote(type: String, userId: String): Int
    fun increase(type: String, userId: String)
}

class MessageSentCounterImpl(private val buckets: List<Cache>) : MessageSentCounter {
    override fun quote(type: String, userId: String): Int {
        return buckets.find { it.keyPrefix == type }?.get(userId) ?: 0
    }

    override fun increase(type: String, userId: String) {
        buckets.find { it.keyPrefix == type }?.save(userId)
    }

}