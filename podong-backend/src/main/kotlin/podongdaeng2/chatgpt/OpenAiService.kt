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
import podongdaeng2.exposed.repository.BasicRepository
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
        // TODO: save all process? including checking completion?
        BasicRepository.insertThread(
            dietAdvisorId.id,
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
            request = RunRequest(assistantId = AssistantId(dietAdvisorId.id)),
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

        BasicRepository.insertMessage(
            assistantIdInput = dietAdvisorId.id,
            messageIdInput = latestMessage.id.id,
            contentInput = latestMessage.content.toString()
        )
        println("DONE REQUEST")

        return latestMessage.content.toString() // seems like ordered in descending order of time?
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

        BasicRepository.insertMessage(
            assistantIdInput = medicalGuesserId.id,
            messageIdInput = latestMessage.id.id,
            contentInput = latestMessage.content.toString()
        )
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
                file = FileSource(path = path, fileSystem = FileSystem.RESOURCES), // automatically searches "/src/main/resources"
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
                    You are a Diet Advisor, which advises a better diet and lifework.
                    You will take an input in Korean describing user's best calorie per day and what user have eaten.
                    - Consider and answer Calories and Carbohydrate and Protein and Fats are appropriate or not
                    - Consider vitamins are appropriate or not
                    - Consider minerals are appropriate or not
                    - Answer your opinion with exercise you recommend to this user
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
                    Let's think step by step.
                    You are a bot that predicts diseases or health problems that caused by certain symptoms. You will take an input from customer.
                    There are several rules for the answer form.

                    - Make a guess of a disease or health problem with the phrase in Korean. A disease must be in excel sheet named disease_list_demo.xlsx I have uploaded.
                    - refer to symptoms provided in excel sheet and compare with input.
                    - If input provided is assumed that the phrase is not describing a symptom for disease, describe the [name of the disease] as 추측불가
                    - You dont need to make an single diagnosis. Guess the possibility of various diseases
                    - When you are responding ALWAYS follow this form
                    ```
                    {
                    "assumes": [
                    {
                    "name": 'name of disease'
                    "symptom": 'representative symtoms or effect',
                    "simple_aids": 'Simple remedies or actions that can be dealt with'
                    }
                    ]
                    }
                    ```

                    Answer by creating JSON in this format in the form of key-value. The key indicated by each double quotation mark should output exactly same, and the value indicated by a single quotation mark can be output according to the instruction. 
                    An actual output should be printed in Korean except key of JSON.
                """.trimIndent(),
                tools = listOf(AssistantTool.RetrievalTool),
                model = ModelId("gpt-3.5-turbo"),
            )
        )
        BasicRepository.insertAssistant(
            openaiIdInput = assistant.id.id,
            instructionInput = assistant.instructions,
            modelInput = assistant.model.id,
        )
    }
}