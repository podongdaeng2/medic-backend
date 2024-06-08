package podongdaeng2.ktor.server

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.run.RunId
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.OpenAI
import exposed.model.RunRequest
import exposed.repository.BasicRepository
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

fun main() {
    val basicRepository = BasicRepository
    val envVar by lazy { getEnvironmentVariables() }
    val apiKey = envVar["CHATGPT_API_KEY"]
    val openAI = OpenAI(
        token = apiKey,
        timeout = Timeout(socket = 60.seconds),
        // additional configurations may follow
    )
    embeddedServer(Netty, port = 8090) {
//        val mutex = Mutex() // needed?

        repeat(Runtime.getRuntime().availableProcessors()) {
            launch(Dispatchers.Default) {
                while (true) {
                    val (firstNotStartedRunRequest, handleStartDateTime) = transaction {
                        val notStartedRunRequests = basicRepository.selectForUpdateNotStartedRunRequests()
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
                        handleOpenAiRequest(openAI, firstNotStartedRunRequest, basicRepository, handleStartDateTime)
                    } else {
                        delay(Duration.ofSeconds(10).toMillis())
                    }
                    println("Loop done by server")
                }
            }
        }

        routing {
            get("/") {
                call.respondText("Hello, World!")
            }
        }
    }.start(wait = true)
}

@OptIn(BetaOpenAI::class)
private suspend fun handleOpenAiRequest(
    openAI: OpenAI,
    firstNotStartedRunRequest: RunRequest,
    basicRepository: BasicRepository,
    handleStartDateTime: LocalDateTime?
) {
    var currentRun = openAI.getRun(
        threadId = ThreadId(firstNotStartedRunRequest.threadId),
        runId = RunId(firstNotStartedRunRequest.runId)
    )
    val intervalMillis = 8000L // TODO-ASYNC: delete after implementing async
    while (true) {
        if (currentRun.completedAt != null) {
            println("RUN COMPLETED ${currentRun.completedAt}")
            break
        } else if (currentRun.failedAt != null) {
            // TODO: Handle failure
            println("FAILED RUN!! ${currentRun.failedAt}")
            break
        }

        delay(intervalMillis) // Wait for the specified interval before the next check
        currentRun = openAI.getRun(
            threadId = ThreadId(firstNotStartedRunRequest.threadId),
            runId = RunId(firstNotStartedRunRequest.runId)
        )
    }

    val latestMessage =
        openAI.messages(threadId = ThreadId(firstNotStartedRunRequest.threadId)).first()
    println(latestMessage.content)
    println()
    openAI.delete(id = ThreadId(firstNotStartedRunRequest.threadId)) // TODO: DB delete
    transaction {
        BasicRepository.insertMessage(
            assistantIdInput = currentRun.assistantId.id,
            messageIdInput = latestMessage.id.id,
            contentInput = latestMessage.content.toString()
        )
        basicRepository.updateHandleDateTimeOfRunRequest(
            uidInput = firstNotStartedRunRequest.uid,
            handleStartDateTime = handleStartDateTime,
            handleCompleteDateTime = LocalDateTime.now(),
        )
    }
}

fun getEnvironmentVariables(): Dotenv {
    return dotenv()
}