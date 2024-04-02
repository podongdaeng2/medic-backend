package podongdaeng2.chatgpt

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.Model
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

object OpenAiService {
    private val apiKey by lazy {
        System.getenv("CHATGPT_API_KEY")
    }
    private val openai = OpenAI(
        token = apiKey,
        timeout = Timeout(socket = 60.seconds),
        // additional configurations...
    )

    suspend fun simpleRequest() {
        val models: List<Model> = openai.models()
        print(models)
    }
}