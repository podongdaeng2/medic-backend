import exposed.repository.BasicRepository
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.LocalDateTime

fun main() {
    embeddedServer(Netty, port = 8090) {
//        val mutex = Mutex() // needed?

        launch(Dispatchers.Default) { // TODO: check if this runs on multithread
            while (true) {
                val notStartedRunRequests = BasicRepository.selectNotStartedRunRequests()
                val firstNotStartedRunRequest = notStartedRunRequests.first()
                val handleStartDateTime = LocalDateTime.now()
                BasicRepository.updateHandleDateTimeOfRunRequest(
                    uidInput = firstNotStartedRunRequest.uid,
                    handleStartDateTime = handleStartDateTime,
                    handleCompleteDateTime = null,
                )

                transaction {
                    Thread.sleep(Duration.ofSeconds(30).toMillis()) // TODO: implement OpenAI API request

                    BasicRepository.updateHandleDateTimeOfRunRequest(
                        uidInput = firstNotStartedRunRequest.uid,
                        handleStartDateTime = handleStartDateTime,
                        handleCompleteDateTime = LocalDateTime.now(),
                    )
                }

                delay(Duration.ofSeconds(10).toMillis())
            }
        }

        routing {
            get("/run-request/{uid}") {
                call.respondText("Hello, World!")
            }
        }
    }.start(wait = true)
}
