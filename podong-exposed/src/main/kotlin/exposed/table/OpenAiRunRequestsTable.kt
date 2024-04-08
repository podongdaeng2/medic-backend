package exposed.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object OpenAiRunRequestsTable : Table("openai_run_requests") {
    val uid: Column<Int> = integer("uid").autoIncrement()
    val runId: Column<String> = varchar("run_id", 50)
    val threadId: Column<String> = varchar("thread_id", 50)
    val registeredDateTime: Column<LocalDateTime> = datetime("registered_datetime")
    override val primaryKey = PrimaryKey(uid)
}