package exposed.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object MessageThreadTable : Table("message_thread") {
    val uid: Column<Int> = integer("uid").autoIncrement()
    val assistantId: Column<String> = varchar("assistant_id", 50)
    val messageId: Column<String> = varchar("message_id", 50)
    val content:Column<String> = varchar("content", 2000)
    override val primaryKey = PrimaryKey(uid)
}