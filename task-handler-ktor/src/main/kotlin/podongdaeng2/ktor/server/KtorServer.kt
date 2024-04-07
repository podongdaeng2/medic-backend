import exposed.repository.BasicRepository
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import java.time.Duration

fun main() {
    embeddedServer(Netty, port = 8090) {
//        val mutex = Mutex() // needed?

        // Launch a background coroutine to perform continuous search
        launch {
            while (true) {
                // Perform the search operation asynchronously
                val result = withContext(Dispatchers.IO) {
                    // DB or Redis search
                    BasicRepository.selectAllAssistant()
                }
                println("Search result: $result")

                // Wait for a specified interval before the next search
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
