package delivery.config

import io.dropwizard.Configuration
import javax.inject.Singleton
import javax.validation.constraints.NotNull

@Singleton
class ServiceConfiguration(val name: String = "ratelimit-poc") : Configuration() {
    val defaultSize: @NotNull Int = 0
}