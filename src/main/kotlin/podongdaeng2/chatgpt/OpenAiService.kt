package podongdaeng2.chatgpt

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.core.Role
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
import podongdaeng2.exposed.repository.BasicRepository
import java.time.Duration
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration.Companion.seconds

object OpenAiService { // by lazy 선언 쓰는법 알아와서 적절한데에 써야할수도
    private val envVar by lazy { getEnvironmentVariables() }
    private val apiKey = envVar["CHATGPT_API_KEY"]
    private val openAI = OpenAI(
        token = apiKey,
        timeout = Timeout(socket = 60.seconds),
        // additional configurations...
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
    suspend fun talkMedicalGuesser(talk: String): String {
        val medicalGuesserId = AssistantId("asst_Yiv8sUTfvAioPRYYuveQ3ABm") // TODO-MINOR: change to DB select
        val thread = openAI.thread()
        // TODO: save all process? including checking completion?
        BasicRepository.insertThread(
            medicalGuesserId.id,
            thread.id.id,
        )
        val messageRequested = openAI.message(
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
                break
            } else if (currentRun.failedAt != null) {
                // TODO: Handle failure
                break
            }
            println()
            delay(intervalMillis) // Wait for the specified interval before the next check
        }

        val latestMessage = openAI.messages(threadId = ThreadId(thread.id.id)).first()
        println()
        println(latestMessage.content)
        println()

        BasicRepository.insertMessage(
            threadIdInput = latestMessage.threadId.id,
            messageIdInput = latestMessage.content.toString(),
            contentInput = latestMessage.content.toString()
        )

        openAI.delete(id = ThreadId(thread.id.id))
        return latestMessage.toString() // seems like ordered in descending order of time?
    }

    private fun getEnvironmentVariables(): Dotenv {
        return dotenv()
    }

    @OptIn(BetaOpenAI::class)
    @Deprecated("ONLY for creating assistant")
    suspend fun createMedicalGuesser() {
        val assistant = openAI.assistant(
            request = AssistantRequest(
                name = "Medical Guesser",
                tools = emptyList(),
                model = ModelId("gpt-4"),
                description = """
                    너는 고령 고객층이 주류인 앱에서 특정 증상을 유발하는 질병이나 건강상의 문제를 추측하는 봇이다. 증상이나 조치에 대해서는 존댓말을 써야 한다.
                    답변 양식에 대해서는 몇 가지 규칙이 존재한다.

                    1. 제시된 문구에 대한 질병이나 건강상의 문제의 추측을 해라.
                    2. 만약 문구가 질병에 대한 증상이 아니라고 추측된다면, 질병의 이름을 “추측불가” 로 추측해라.
                    3. 정확한 진단을 내릴 필요는 없다. 여러 가지 질병의 가능성을 추측해라.
                    4. 답변을 내릴때,

                    ```
                    {
                    '각 질병의 이름': {
                    "증상": '대표적인 증상이나 나쁜 점',
                    "간단한 조치": '대처 가능한 간단한 민간요법이나 스트레칭'
                    }
                    }
                    ```

                    으로 key-value 형태로 JSON 형태로 만들어서 답변하라. 따옴표 한개는 입력할 주제고, 따옴표 두개의 경우 글자 그대로 출력하면 된다.

                """.trimIndent()
            )
        )
        BasicRepository.insertAssistant(
            openaiIdInput = assistant.id.id,
            instructionInput = assistant.instructions,
            modelInput = assistant.model.id,
        )
    }

    @OptIn(BetaOpenAI::class)
    @Deprecated("ONLY for MODIFYING assistant")
    suspend fun modifyMedicalGuesser() {
        val assistant = openAI.assistant(
            id = AssistantId("asst_Yiv8sUTfvAioPRYYuveQ3ABm"), request = AssistantRequest(
                description = "포동댕의 질병추론기",
                instructions = """
                    너는 고령 고객층이 주류인 앱에서 특정 증상을 유발하는 질병 혹은 건강상의 문제를 추측하는 봇이다. 증상이나 조치에 대해서는 존댓말을 써야 한다.
                    답변 양식에 대해서는 몇 가지 규칙이 존재한다.

                    1. 제시된 문구에 대한 질병이나 건강상의 문제의 추측을 해라. 질병의 경우 현실에 존재하는, 실존하는 질병이어야 한다.
                    2. 만약 문구가 질병에 대한 증상이 아니라고 추측된다면, 질병의 이름을 “추측불가” 로 추측해라.
                    3. 정확한 진단을 내릴 필요는 없다. 여러 가지 질병의 가능성을 추측해라.
                    4. 답변을 내릴때,

                    ```
                    {
                    "assumes": [
                    {
                    "name": '각 질병의 이름'
                    "symptom": '대표적인 증상이나 나쁜 점',
                    "simple_aids": '대처 가능한 간단한 민간요법이나 스트레칭'
                    }
                    ]
                    }
                    ```

                    이러한 형식으로 key-value 형태로 JSON 형태로 만들어서 답변하라. 각 쌍따옴표로 표시한 key는 지시한 대로 출력하면 되고, 단일 따옴표로 표시한 value는 지시한 바에 맞게 출력하면 된다. 실제로 출력할 때는 모두 JSON 형식에 맞게 쌍따옴표를 쓴다.

                """.trimIndent(),
                tools = emptyList(),
                model = ModelId("gpt-4"),
            )
        )
        BasicRepository.insertAssistant(
            openaiIdInput = assistant.id.id,
            instructionInput = assistant.instructions,
            modelInput = assistant.model.id,
        )
    }
}