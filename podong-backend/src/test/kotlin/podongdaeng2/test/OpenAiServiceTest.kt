package podongdaeng2.test

import enums.ApiRequestTypeEnum
import org.jetbrains.exposed.sql.Database
import org.junit.Test
import exposed.repository.BasicRepository
import kotlinx.coroutines.runBlocking
import net.bytebuddy.utility.RandomString
import org.jetbrains.exposed.sql.transactions.transaction
import podongdaeng2.chatgpt.OpenAiService


class OpenAiServiceTest {
    init {
        Database.connect(
            "jdbc:mysql://localhost:3306/podongdaeng2", driver = "com.mysql.cj.jdbc.Driver",
            user = "root", password = "San1234!"
        )
    }

    @Test
    fun retrieveAiModelList() {
        runBlocking {
            OpenAiService.listModels()
        }
    }
}
