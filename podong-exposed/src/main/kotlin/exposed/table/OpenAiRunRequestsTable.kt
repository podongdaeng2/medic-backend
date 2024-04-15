package exposed.table

import enums.ApiRequestTypeEnum
import exposed.model.RunRequest
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object OpenAiRunRequestsTable : Table("openai_run_requests") {
    val uid: Column<Int> = integer("uid").autoIncrement()
    val runId: Column<String> = varchar("run_id", 50)
    val threadId: Column<String> = varchar("thread_id", 50)
    val handleStartDateTime: Column<LocalDateTime?> = datetime("handle_start_datetime").nullable()
    val handleCompletedDateTime: Column<LocalDateTime?> = datetime("handle_completed_datetime").nullable()
    val retryCount: Column<Int> = integer("retry_count")
    val apiRequestType: Column<ApiRequestTypeEnum> = customEnumeration(
        "api_type",
        fromDb = { value -> ApiRequestTypeEnum.valueOf(value as String) },
        toDb = { it.name }
    )
    val registeredDateTime: Column<LocalDateTime> = datetime("registered_datetime")
    override val primaryKey = PrimaryKey(uid)
}