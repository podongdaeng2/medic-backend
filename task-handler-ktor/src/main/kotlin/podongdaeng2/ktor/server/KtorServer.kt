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
    val basicRepository = BasicRepository // TODO - 이게 최선인가?
    embeddedServer(Netty, port = 8090) {
//        val mutex = Mutex() // needed?

        repeat(Runtime.getRuntime().availableProcessors()) {
            launch(Dispatchers.Default) { // TODO: 8 threads, can be modified?
                while (true) {
                    val (firstNotStartedRunRequest, handleStartDateTime) = transaction {
                        val notStartedRunRequests = basicRepository.selectNotStartedRunRequests()
                        val firstNotStartedRunRequest = notStartedRunRequests.firstOrNull()
                        val handleStartDateTime = LocalDateTime.now()
                        if (firstNotStartedRunRequest != null) {
                            println(firstNotStartedRunRequest.uid)
                            basicRepository.updateHandleDateTimeOfRunRequest(
                                uidInput = firstNotStartedRunRequest.uid,
                                handleStartDateTime = handleStartDateTime,
                                handleCompleteDateTime = null,
                            )
                        }
                        Pair(firstNotStartedRunRequest, handleStartDateTime)
                    }

                    if (firstNotStartedRunRequest != null) {
                        transaction {
                            Thread.sleep(Duration.ofSeconds((Math.random() * 10).toLong()).toMillis()) // TODO: implement OpenAI API request

                            basicRepository.updateHandleDateTimeOfRunRequest(
                                uidInput = firstNotStartedRunRequest.uid,
                                handleStartDateTime = handleStartDateTime,
                                handleCompleteDateTime = LocalDateTime.now(),
                            )
                        }
                    } else {
                        delay(Duration.ofSeconds(10).toMillis())
                    }
                    println("Loop done by server")
                }
            }
        }

        routing {
            get("/run-request/{uid}") {
                call.respondText("Hello, World!")
            }
        }
    }.start(wait = true)
}
