package delivery.rest

import com.fasterxml.jackson.annotation.JsonProperty
import domain.usecase.SendNotificationUseCase
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.*
import javax.ws.rs.core.Response

@Path("/notification")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
class NotificationController(private val sendNotificationUseCase: SendNotificationUseCase) {

    @POST
    fun dispatch(request: SendNotificationRequest): Response {
        sendNotificationUseCase.invoke(request.type, request.userId, request.message)
        return Response.accepted().build()
    }
}

data class SendNotificationRequest(
    @JsonProperty("user_id")
    val userId: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("message")
    val message: String
)