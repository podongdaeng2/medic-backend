package podongdaeng2.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import podongdaeng2.chatgpt.OpenAiService


@RestController
class MainController {
    @GetMapping("/list-assistants")
    suspend fun listAssistants(): List<String> {
//        BasicRepository.basicInsert()
        return OpenAiService.listAssistants()
    }

    @GetMapping("/talk-medical-guesser")
    suspend fun talkMedicalGuesser(@RequestParam talk: String): String {
        return OpenAiService.talkMedicalGuesser(talk)
    }

    @GetMapping("/modify-medical-guesser")
    suspend fun modifyMedicalGuesser() {
        OpenAiService.modifyMedicalGuesser()
    }
}