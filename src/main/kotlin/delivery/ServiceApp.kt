package delivery

import io.dropwizard.Application
import io.dropwizard.setup.Environment


class ServiceApp : Application<ServiceConfiguration>() {

    override fun run(configuration: ServiceConfiguration, environment: Environment) {
        val defaultSize: Int = configuration.defaultSize

        environment
            .healthChecks()
            .register("application", ApplicationHealthCheck())
    }
}