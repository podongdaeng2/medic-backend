package podongdaeng2.exposed.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ThreadAssistantTable : Table("thread_assistant") {
    val uid: Column<Int> = integer("uid").autoIncrement()
    val assistantId: Column<String> = varchar("assistant_id", 50)
    val threadId: Column<String> = varchar("thread_id", 50)
    override val primaryKey = PrimaryKey(uid)
}