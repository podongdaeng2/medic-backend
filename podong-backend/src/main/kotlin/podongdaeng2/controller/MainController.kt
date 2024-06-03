package podongdaeng2.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import podongdaeng2.chatgpt.OpenAiService


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

    @GetMapping("/talk-diet-advisor")
    suspend fun talkDietAdvisor(
        @RequestParam healthData: String,
        @RequestParam talk: String? = null,
    ): String {
        val stringInput = """
            ${talk?.let { "user input: $talk" } ?: ""}
            $healthData
        """.trimIndent() // TODO: make service
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