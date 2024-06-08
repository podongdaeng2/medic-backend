package podongdaeng2.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import podongdaeng2.chatgpt.OpenAiService
import podongdaeng2.chatgpt.SimpleService


@RestController
class MainController {

    @GetMapping("/")
    suspend fun healthCheck(): String {
        return "i am healthy!"
    }

    @GetMapping("/list-assistants")
    suspend fun listAssistants(): List<String> {
//        BasicRepository.basicInsert()
        return OpenAiService.listAssistants()
    }

    @PostMapping("/talk-diet-advisor")
    suspend fun talkDietAdvisor(
        @RequestParam("user_uuid") userUuid: String,
        @RequestParam("food_intake") foodIntakeCsvFileString: String? = null,
        @RequestParam("food_info") foodInfoCsvFileString: String? = null,
        @RequestParam("user_info") userInfo: String,
        @RequestParam("user_input") userInput: String? = null
    ): String {
        return if (foodInfoCsvFileString != null && foodIntakeCsvFileString != null) {
            // AI 통신
            val openAiInputString = SimpleService.getOpenAiInputString(
                rawFoodIntakeCsvStringData = foodIntakeCsvFileString,
                rawFoodInfoCsvStringData = foodInfoCsvFileString,
                userInfo = userInfo,
                userInput = userInput ?: "",
            )
            return OpenAiService.talkDietAdvisor(openAiInputString)
        } else if (foodInfoCsvFileString == null && foodIntakeCsvFileString == null) {
            // 단순 통신
            "dansun tongshin"
        } else {
            throw Exception("only one file is null.")
        }
    }

    // TODO: remove
    @GetMapping("/talk-diet-advisor-alpha")
    suspend fun talkDietAdvisor(
        @RequestParam healthData: String,
        @RequestParam talk: String? = null,
    ): String {
        val stringInput = """
            ${talk?.let { "user input: $talk" } ?: ""}
            $healthData
        """.trimIndent()
        return OpenAiService.talkDietAdvisor(healthData + talk)
    }

    @GetMapping("/talk-medical-guesser")
    suspend fun talkMedicalGuesser(@RequestParam talk: String): String {
        return OpenAiService.talkMedicalGuesser(talk)
    }


    @GetMapping("/modify-assistant")
    suspend fun modifyAssistant() {
        OpenAiService.modifyAssistant()
    }

    @GetMapping("/upload-file")
    suspend fun uploadFile() {
//        OpenAiService.uploadFile()
    }
}