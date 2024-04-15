package exposed.model

import enums.ApiRequestTypeEnum
import java.time.LocalDateTime

data class RunRequest(
    val uid: Int,
    val runId: String,
    val threadId: String,
    val handleStartDateTime: LocalDateTime?,
    val handleCompletedDateTime: LocalDateTime?,
    val retryCount: Int,
    val apiType: ApiRequestTypeEnum,
    val registeredDateTime: LocalDateTime,
)
