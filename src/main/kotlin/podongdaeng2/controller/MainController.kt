package podongdaeng2.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import podongdaeng2.chatgpt.OpenAiService


@RestController
class MainController {
    @GetMapping("/hello")
    suspend fun listAssistants(@RequestParam name: String): String {
//        BasicRepository.basicInsert()
        OpenAiService.listAssistants()
        return "Hello, $name!"
    }

    @GetMapping("/talk-medical-guesser")
    suspend fun talkMedicalGuesser(@RequestParam talk: String): String {
        return OpenAiService.talkMedicalGuesser(talk)
    }
}