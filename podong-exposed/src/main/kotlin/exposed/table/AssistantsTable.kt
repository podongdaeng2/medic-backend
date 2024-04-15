package exposed.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object AssistantsTable : Table("Assistants") {
    val uid: Column<Int> = integer("uid").autoIncrement()
    val openaiId: Column<String> = varchar("openai_id", 50)
    val instruction: Column<String?> = varchar("instructions", 2000).nullable()
    val model: Column<String> = varchar("model", 30)
    override val primaryKey = PrimaryKey(uid)
}