package podongdaeng2.chatgpt

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.message.TextContent
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.RunId
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.OpenAI
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.delay
import okio.FileSystem
import okio.Path.Companion.toPath
import exposed.repository.BasicRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jetbrains.exposed.sql.transactions.transaction
import enums.ApiRequestTypeEnum
import io.ktor.util.reflect.*
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

object OpenAiService { // by lazy 선언 쓰는법 알아와서 적절한데에 써야할수도
    private val envVar by lazy { getEnvironmentVariables() }
    private val apiKey = envVar["CHATGPT_API_KEY"]
    private val openAI = OpenAI(
        token = apiKey,
        timeout = Timeout(socket = 60.seconds),
        // additional configurations may follow
    )

    suspend fun listModels() {
        val models = openAI.models()
        print(models)
    }

    @OptIn(BetaOpenAI::class) // beta features marked like this
    suspend fun listAssistants(): List<String> {
        val assistants = openAI.assistants()
        assistants.forEach {
            println(it)
        }
        return assistants.map { it.id.id + " " + it.description.toString() }
    }

    @OptIn(BetaOpenAI::class)
    suspend fun talkDietAdvisor(talk: String): String {
        val dietAdvisorId = AssistantId("asst_7ubPljm7e2SXlTjCiJhXthbH") // TODO-MINOR: change to DB select

        val thread = openAI.thread()
        transaction {
            BasicRepository.insertThread(
                dietAdvisorId.id,
                thread.id.id,
            )
        }

        openAI.message(
            threadId = ThreadId(thread.id.id),
            request = MessageRequest(
                role = Role.User,
                content = talk,
            )
        )
        val run = openAI.createRun(
            threadId = ThreadId(thread.id.id),
            request = RunRequest(assistantId = AssistantId(dietAdvisorId.id)),
        )

        val runUid = transaction {
            BasicRepository.insertRunRequests(
                runIdInput = run.id.id,
                threadIdInput = thread.id.id,
                apiTypeInput = ApiRequestTypeEnum.DIET_ADVISOR,
            )
        }

        return handleOpenAiRequest(
            threadId = thread.id,
            runId = run.id,
        )
    }

    @OptIn(BetaOpenAI::class)
    suspend fun talkMedicalGuesser(talk: String): String {
        val medicalGuesserId = AssistantId("asst_Yiv8sUTfvAioPRYYuveQ3ABm") // TODO-MINOR: change to DB select
        val thread = openAI.thread()
        transaction {
            BasicRepository.insertThread(
                medicalGuesserId.id,
                thread.id.id,
            )
        }
        openAI.message(
            threadId = ThreadId(thread.id.id),
            request = MessageRequest(
                role = Role.User,
                content = talk,
            )
        )
        val run = openAI.createRun(
            threadId = ThreadId(thread.id.id),
            request = RunRequest(assistantId = AssistantId(medicalGuesserId.id)),
        )

        val intervalMillis = 8000L
        while (true) {
            val currentRun = openAI.getRun(
                threadId = ThreadId(thread.id.id),
                runId = RunId(run.id.id)
            )

            if (currentRun.completedAt != null) {
                println("RUN COMPLETED ${run.completedAt}")
                break
            } else if (currentRun.failedAt != null) {
                // TODO: Handle failure
                println("FAILED RUN!! ${run.failedAt}")
                break
            }
            println()
            delay(intervalMillis) // Wait for the specified interval before the next check
        }

        val latestMessage = openAI.messages(threadId = ThreadId(thread.id.id)).first()
        println() // TODO-MINOR-DELETE: for logs, delete
        println(latestMessage.content)
        println()
        openAI.delete(id = ThreadId(thread.id.id)) // TODO: DB delete

        transaction {
            BasicRepository.insertMessage(
                assistantIdInput = medicalGuesserId.id,
                messageIdInput = latestMessage.id.id,
                contentInput = latestMessage.content.toString()
            )
        }
        println("DONE REQUEST")

        return latestMessage.toString() // seems like ordered in descending order of time?
    }

    private fun getEnvironmentVariables(): Dotenv {
        return dotenv()
    }

    suspend fun uploadFile() {
        val filePath = "/disease_list_demo.xlsx" // TODO: change to Parameter or Migration page.
        val path = filePath.toPath()
        val file = openAI.file(
            request = FileUpload(
                file = FileSource(
                    path = path,
                    fileSystem = FileSystem.RESOURCES
                ), // automatically searches "/src/main/resources"
                purpose = Purpose("assistants")
            )
        )
        println()
        println(file)
        println()
        println(file.id)
    }

    @OptIn(BetaOpenAI::class)
    @Deprecated("ONLY for creating assistant")
    suspend fun createAssistant() {
        val assistant = openAI.assistant(
            request = AssistantRequest(
                name = "Diet Advisor",
                tools = emptyList(),
                model = ModelId("gpt-3.5-turbo"),
                instructions = """
                    Let's think step by step.
                    # CONTEXT #
                    you are a very good nutritionist. People will give you the food they eat, the detailed nutritional content of that food, and the person's body information, personal opinion as input

                    # OBJECTIVE #
                    - give recommendations for these categories: next meal to eat, several exercise name to recommend, and overall health advice solutions.
                    - if specific nutrition taken is high or low, select recommendations to balance it. if this is done, comment it in overall advice. 
                    - give some side dishes when recommending meal.
                    - exercise recommendation should include detailed sets and times.
                    - Meal recommendation and exercise must reflect personal opinion.
                    - overall health advice can give side options for health.

                    # STYLE #
                    write it in without commanding-style

                    # TONE #
                    positive and encouraging tone

                    # AUDIENCE #
                    My client is Korean who wants to live a long life by making his health in its best condition

                    # RESPONSE #
                    Do not use special character, but divide each category with linebreak character at the end. recommending meal or exercises should be divided in comma, but not at the end. respond in Korean.
                    
                    here is example of conversations:
                    #
                    다음 식사 추천
                    단백질이 풍부한 두부 스테이크, 구운 닭가슴살, 퀴노아 샐러드, 삶은 달걀
                    곁들일 수 있는 반찬으로는 나물무침, 김치, 오이무침을 추천드립니다
                    
                    운동 추천
                    상체 운동: 푸쉬업 3세트, 각각 12회
                    하체 운동: 스쿼트 3세트, 각각 15회
                    유산소 운동: 걷기 30분, 일주일에 3회
                    
                    전체적인 건강 조언
                    단백질 섭취를 늘리고 염분을 줄이는 식단을 잘 유지하고 계신 점이 아주 좋습니다. 다음 식사로는 단백질이 풍부하고 염분이 적은 식품들을 추천드렸습니다. 운동은 전신을 고루 강화할 수 있는 상체와 하체 운동을 포함하여, 유산소 운동도 함께 하여 심폐 건강을 챙기세요. 추가적으로 충분한 수분 섭취와 규칙적인 수면도 중요합니다. 건강한 식단과 운동을 꾸준히 이어가시면 더욱 건강한 삶을 유지하실 수 있을 것입니다.
                    #
                """.trimIndent()
            )
        )
        transaction {
            BasicRepository.insertAssistant(
                openaiIdInput = assistant.id.id,
                instructionInput = assistant.instructions,
                modelInput = assistant.model.id,
            )
        }
    }

    @OptIn(BetaOpenAI::class)
    @Deprecated("ONLY for MODIFYING assistant")
    suspend fun modifyAssistant() {
        val assistant = openAI.assistant(
            id = AssistantId("asst_7ubPljm7e2SXlTjCiJhXthbH"), request = AssistantRequest(
                description = "포동댕의 영양조언기",
                instructions = """
                    Let's think step by step.
                    # CONTEXT #
                    you are a very good nutritionist. People will give you the food they eat, the detailed nutritional content of that food, and the person's body information, personal opinion as input

                    # OBJECTIVE #
                    - give recommendations for these categories: next meal to eat, several exercise name to recommend, and overall health advice solutions.
                    - if specific nutrition taken is high or low, select recommendations to balance it. if this is done, comment it in overall advice. 
                    - give some side dishes when recommending meal.
                    - exercise recommendation should include detailed sets and times.
                    - Meal recommendation and exercise must reflect personal opinion.
                    - overall health advice can give side options for health.

                    # STYLE #
                    write it in without commanding-style

                    # TONE #
                    positive and encouraging tone

                    # AUDIENCE #
                    My client is Korean who wants to live a long life by making his health in its best condition

                    # RESPONSE #
                    Do not use special character, but divide each category with linebreak character at the end. recommending meal or exercises should be divided in comma, but not at the end. respond in Korean.
                    
                    here is example of conversations:
                    #
                    다음 식사 추천
                    단백질이 풍부한 두부 스테이크, 구운 닭가슴살, 퀴노아 샐러드, 삶은 달걀
                    곁들일 수 있는 반찬으로는 나물무침, 김치, 오이무침을 추천드립니다
                    
                    운동 추천
                    상체 운동: 푸쉬업 3세트, 각각 12회
                    하체 운동: 스쿼트 3세트, 각각 15회
                    유산소 운동: 걷기 30분, 일주일에 3회
                    
                    전체적인 건강 조언
                    단백질 섭취를 늘리고 염분을 줄이는 식단을 잘 유지하고 계신 점이 아주 좋습니다. 다음 식사로는 단백질이 풍부하고 염분이 적은 식품들을 추천드렸습니다. 운동은 전신을 고루 강화할 수 있는 상체와 하체 운동을 포함하여, 유산소 운동도 함께 하여 심폐 건강을 챙기세요. 추가적으로 충분한 수분 섭취와 규칙적인 수면도 중요합니다. 건강한 식단과 운동을 꾸준히 이어가시면 더욱 건강한 삶을 유지하실 수 있을 것입니다.
                    #
                """.trimIndent(),
                tools = listOf(),
                model = ModelId("gpt-4-turbo"),
            )
        )
        transaction {
            BasicRepository.insertAssistant(
                openaiIdInput = assistant.id.id,
                instructionInput = assistant.instructions,
                modelInput = assistant.model.id,
            )
        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun handleOpenAiRequest(
        threadId: ThreadId,
        runId: RunId,
    ): String {
        var currentRun = openAI.getRun(
            threadId = threadId,
            runId = runId,
        )
        val intervalMillis = 8000L
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
                threadId = threadId,
                runId = runId,
            )
        }

        val latestMessage = openAI.messages(threadId = threadId)
            .first()

        val latestMessageString = latestMessage.content.toString().substringAfter("value=").substringBefore(", annotations=")
        println(latestMessage.content)
        println()
        openAI.delete(id = threadId)
        transaction {
            BasicRepository.insertMessage(
                assistantIdInput = currentRun.assistantId.id,
                messageIdInput = latestMessage.id.id,
                contentInput = latestMessageString
            )
        }

        return latestMessageString
    }
}