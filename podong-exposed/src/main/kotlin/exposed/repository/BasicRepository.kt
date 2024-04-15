package exposed.repository

import enums.ApiRequestTypeEnum
import exposed.model.RunRequest
import exposed.table.AssistantsTable
import exposed.table.MessageThreadTable
import exposed.table.OpenAiRunRequestsTable
import exposed.table.ThreadAssistantTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

object BasicRepository {
    init {
        ExposedDatabase.connect()
    }

    // Exposed table name and argument of function should be different. 짜치네...
    fun insertAssistant(openaiIdInput: String, instructionInput: String?, modelInput: String): String {
        val id = AssistantsTable.insert {
            it[openaiId] = openaiIdInput
            it[instruction] = instructionInput
            it[model] = modelInput
        } get AssistantsTable.openaiId

        return id
    }

    fun insertThread(assistantIdInput: String, threadIdInput: String): String {
        val id = ThreadAssistantTable.insert {
            it[assistantId] = assistantIdInput
            it[threadId] = threadIdInput
        } get ThreadAssistantTable.threadId
        return id
    }

    fun insertMessage(assistantIdInput: String, messageIdInput: String, contentInput: String): String {
        val id = MessageThreadTable.insert {
            it[assistantId] = assistantIdInput
            it[messageId] = messageIdInput
            it[content] = contentInput
        } get MessageThreadTable.messageId
        return id
    }

    fun insertRunRequests(runIdInput: String, threadIdInput: String, apiTypeInput: ApiRequestTypeEnum): Int {
        val id = transaction {
            val now = LocalDateTime.now()
            OpenAiRunRequestsTable.insert {
                it[runId] = runIdInput
                it[threadId] = threadIdInput
                it[registeredDateTime] = now
                it[handleStartDateTime] = null
                it[handleCompletedDateTime] = null
                it[apiRequestType] = apiTypeInput
                it[retryCount] = 0
            } get OpenAiRunRequestsTable.uid
        }

        return id
    }

    fun selectAllAssistant(): List<Pair<String, String>> {
        return AssistantsTable.selectAll().map {
            it[AssistantsTable.openaiId] to it[AssistantsTable.model]
        }
    }

    fun selectForUpdateNotStartedRunRequests(): List<RunRequest> {
        return OpenAiRunRequestsTable.selectAll().where {
            OpenAiRunRequestsTable.handleStartDateTime eq null
        }.forUpdate().map {
            RunRequest(
                uid = it[OpenAiRunRequestsTable.uid],
                runId = it[OpenAiRunRequestsTable.runId],
                threadId = it[OpenAiRunRequestsTable.threadId],
                handleStartDateTime = it[OpenAiRunRequestsTable.handleStartDateTime],
                handleCompletedDateTime = it[OpenAiRunRequestsTable.handleCompletedDateTime],
                retryCount = it[OpenAiRunRequestsTable.retryCount],
                apiType = it[OpenAiRunRequestsTable.apiRequestType],
                registeredDateTime = it[OpenAiRunRequestsTable.registeredDateTime],
            )
        }
    }

    fun updateHandleDateTimeOfRunRequest(
        uidInput: Int,
        handleStartDateTime: LocalDateTime?,
        handleCompleteDateTime: LocalDateTime?
    ) {
        transaction {
            OpenAiRunRequestsTable.update({ OpenAiRunRequestsTable.uid eq uidInput }) {
                it[OpenAiRunRequestsTable.handleStartDateTime] = handleStartDateTime
                it[OpenAiRunRequestsTable.handleCompletedDateTime] = handleCompleteDateTime
            }
        }
    }
}