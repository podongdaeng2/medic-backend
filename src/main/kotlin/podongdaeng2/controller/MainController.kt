package podongdaeng2.controller

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import podongdaeng2.chatgpt.OpenAiService
import podongdaeng2.exposed.repository.BasicRepository
import podongdaeng2.exposed.table.TestTable


@RestController
class MainController {
    @GetMapping("/hello")
    suspend fun sayHello(@RequestParam name: String): String {
//        BasicRepository.basicInsert()
        OpenAiService.simpleRequest()
        return "Hello, $name!"
    }
}