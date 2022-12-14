package delivery
import com.codahale.metrics.health.HealthCheck


class ApplicationHealthCheck : HealthCheck() {
    @Throws(Exception::class)
    override fun check(): Result {
        return Result.healthy()
    }
}