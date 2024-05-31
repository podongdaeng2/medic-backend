package podongdaeng2.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import podongdaeng2.chatgpt.SimpleService


@RestController
class QAController { // TODO: remove when making Prod server
    @PostMapping("/string-input-openai")
    suspend fun talkDietAdvisor(
        @RequestParam("user_uid") userUid: Int,
        @RequestParam("food_intake") foodIntakeCsvFileString: String,
        @RequestParam("food_info") foodInfoCsvFileString: String,
        @RequestParam("user_info") userInfo: String,
        @RequestParam("user_input") userInput: String? = null
    ): String {
        return SimpleService.getOpenAiInputString(
            rawFoodIntakeCsvStringData = foodIntakeCsvFileString,
            rawFoodInfoCsvStringData = foodInfoCsvFileString,
            userInfo = userInfo,
            userInput = userInput ?: "",
        )
    }
}