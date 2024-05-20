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

    @GetMapping("/talk-diet-advisor")
    suspend fun talkDietAdvisor(@RequestParam talk: String): String {
        val healthData = """
            cholesterol
            116.000000
            potassium
            509.000000
            sodium
            1000.000000
            trans_fat
            0.300000
            carbohydrate
            89.150000
            calcium
            213.000000
            monosaturated_fat
            8.621000
            sugar
            36.09
            saturated_fat
            15.016
            vitamin_a
            62.000000
            vitamin_c
            3.000000
            calorie
            880
            protein
            35.190000
            total_fat
            44.610000
            dietary_fiber
            0.600000
            iron
            5.000000
            polysaturated_fat
            3.718

            Sugar coffee, McDonald's bulgogi burger single, 4 pieces of seasoned chicken, 200ml of coke

            70kg / 172cm / male
        """.trimIndent() // TODO-FRONT / SAN
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