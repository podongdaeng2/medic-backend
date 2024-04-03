package podongdaeng2.exposed.repository

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import podongdaeng2.exposed.table.ThreadAssistantTable
import podongdaeng2.exposed.table.AssistantsTable
import podongdaeng2.exposed.table.MessageThreadTable

object BasicRepository {
    // Exposed table name and argument of function should be different. 짜치네...
    fun insertAssistant(openaiIdInput: String, instructionInput: String?, modelInput: String): String {
        val id = transaction {
            AssistantsTable.insert {
                it[openaiId] = openaiIdInput
                it[instruction] = instructionInput
                it[model] = modelInput
            } get AssistantsTable.openaiId
        }
        return id
    }

    fun insertThread(assistantIdInput: String, threadIdInput: String,): String {
        val id = transaction {
            ThreadAssistantTable.insert {
                it[assistantId] = assistantIdInput
                it[threadId] = threadIdInput
            } get ThreadAssistantTable.threadId
        }
        return id
    }

    fun insertMessage(threadIdInput: String, messageIdInput: String, contentInput: String): String {
        val id = transaction {
            MessageThreadTable.insert {
                it[threadId] = threadIdInput
                it[messageId] = messageIdInput
                it[content] = contentInput
            } get MessageThreadTable.messageId
        }
        return id
    }
}