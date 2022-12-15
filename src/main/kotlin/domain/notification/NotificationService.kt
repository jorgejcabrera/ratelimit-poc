package domain.notification

interface NotificationService {
    fun send(type: String, userId: String, message: String)
}

class NotificationServiceImpl : NotificationService {
    override fun send(type: String, userId: String, message: String) {
        println("sending message $message")
    }

}