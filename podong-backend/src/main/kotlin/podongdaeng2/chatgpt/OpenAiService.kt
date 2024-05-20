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

        return "openAI 요청 완료"
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
                    - exercise recommendation should include detail sets and times.
                    - Meal recommendation and exercise must reflect personal opinion.
                    - overall health advice can give side options for health.

                    # STYLE #
                    write it in an intuitive and easy-to-understand style, without commanding-style

                    # TONE #
                    positive and encouraging tone

                    # AUDIENCE #
                    My client is Korean who wants to live a long life by making his health in its best condition

                    # RESPONSE #
                    Do not use special character, but divide each category with linebreak character at the end. recommending meal or exercises should be divided in comma, but not at the end. respond in Korean.
                    
                    here is example of converstaions:
                    
                    #
                    personal opinion: 저염 음식이 먹고싶고, 운동은 격렬하게 할래
                    
                    다음 식사 추천:
                    현미밥, 구운 연어, 미소 된장국, 오이 무침, 김치
                    
                    운동 추천:
                    HIIT (고강도 인터벌 트레이닝) - 20분 (1분 운동, 1분 휴식)
                    스쿼트 - 3세트, 각 세트당 15회
                    푸쉬업 - 3세트, 각 세트당 15회
                    
                    전체 건강 조언:
                    당분 섭취를 줄이는 것이 좋겠어요. 오늘 섭취한 당분이 많으니 내일은 과일과 채소로 대체하는 것을 추천합니다. 또한, 비타민 A와 C의 섭취를 늘려보세요. 당근이나 파프리카를 식단에 추가하는 것이 좋습니다. 유연성 운동을 하면서 전반적인 스트레스 관리도 신경 써주세요.
                    꾸준히 건강한 식습관을 유지하고 다양한 영양소를 섭취하며 유연성 운동을 통해 몸과 마음의 균형을 맞춰보세요.
                    #
                    
                    #
                    personal opinion: 라면과 관련한 음식이 먹고싶고, 운동은 가벼운걸로 할래
                    
                    다음 식사 추천: 
                    참깨라면, 김치, 삶은 계란, 오이무침, 두부조림
                    
                    운동 추천:
                    걷기: 30분
                    스트레칭: 10분
                    
                    전반적인 건강 조언:
                    현재 섭취한 식사에서 당분과 나트륨이 높으므로 다음 식사에서는 야채와 과일을 포함한 식단을 추천합니다.
                    물을 많이 마시고, 가벼운 운동을 꾸준히 해보세요.
                    라면을 먹고 싶다면, 저염 라면을 선택하거나 야채를 추가해서 드세요.
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
                    - exercise recommendation should include detail sets and times.
                    - Also, do not give additional description for meals and exercise, only give name of each subject. 
                    - Meal recommendation and exercise must reflect personal opinion.
                    - overall health advice can give side options for health.

                    # STYLE #
                    write it in an intuitive and easy-to-understand style, without commanding-style

                    # TONE #
                    positive and encouraging tone

                    # AUDIENCE #
                    My client is Korean who wants to live a long life by making his health in its best condition

                    # RESPONSE #
                    Do not use special character, but divide each category with linebreak character at the end. recommending meal or exercises should be divided in comma, but not at the end. respond in Korean.
                    
                    Example of conversations:
                    #
                    personal opinion: 저염 음식이 먹고싶고, 운동은 격렬하게 할래
                    
                    다음 식사 추천:
                    현미밥, 구운 연어, 미소 된장국, 오이 무침, 김치
                    
                    운동 추천:
                    HIIT (고강도 인터벌 트레이닝) - 20분 (1분 운동, 1분 휴식)
                    스쿼트 - 3세트, 각 세트당 15회
                    푸쉬업 - 3세트, 각 세트당 15회
                    
                    전체 건강 조언:
                    당분 섭취를 줄이는 것이 좋겠어요. 오늘 섭취한 당분이 많으니 내일은 과일과 채소로 대체하는 것을 추천합니다. 또한, 비타민 A와 C의 섭취를 늘려보세요. 당근이나 파프리카를 식단에 추가하는 것이 좋습니다. 유연성 운동을 하면서 전반적인 스트레스 관리도 신경 써주세요.
                    꾸준히 건강한 식습관을 유지하고 다양한 영양소를 섭취하며 유연성 운동을 통해 몸과 마음의 균형을 맞춰보세요.
                    #
                    
                    #
                    personal opinion: 라면과 관련한 음식이 먹고싶고, 운동은 가벼운걸로 할래
                    
                    다음 식사 추천: 
                    참깨라면, 김치, 삶은 계란, 오이무침, 두부조림
                    
                    운동 추천:
                    걷기: 30분
                    스트레칭: 10분
                    
                    전반적인 건강 조언:
                    현재 섭취한 식사에서 당분과 나트륨이 높으므로 다음 식사에서는 야채와 과일을 포함한 식단을 추천합니다.
                    물을 많이 마시고, 가벼운 운동을 꾸준히 해보세요.
                    라면을 먹고 싶다면, 저염 라면을 선택하거나 야채를 추가해서 드세요.
                    #
                """.trimIndent(),
                tools = listOf(),
                model = ModelId("gpt-3.5-turbo"),
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

    suspend fun makeRunRequest(
        requestTypeEnum: ApiRequestTypeEnum,
        runUid: Int
    ) { // TODO-erase: won't use at a high chance
        val client = HttpClient() {
            // more config can be here
        }
        try {
            val response: HttpResponse =
                client.get("http://localhost:8090/run-request/") // TODO: CHANGE AFTER SERVER PUBLISHING. may diff between environment
            println("Response from server: $response")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        } finally {
            client.close()
        }
    }
}