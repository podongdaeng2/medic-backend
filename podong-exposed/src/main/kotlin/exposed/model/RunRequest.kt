package exposed.model

import java.time.LocalDateTime

data class RunRequest(
    val uid: Int,
    val runId: String,
    val threadId: String,
    val handleStartDateTime: LocalDateTime?,
    val handleCompletedDateTime: LocalDateTime?,
    val retryCount: Int,
    val registeredDateTime: LocalDateTime,
)
