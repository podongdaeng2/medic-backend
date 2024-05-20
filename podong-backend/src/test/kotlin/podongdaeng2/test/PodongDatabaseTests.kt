package podongdaeng2.test

import enums.ApiRequestTypeEnum
import org.jetbrains.exposed.sql.Database
import org.junit.Test
import exposed.repository.BasicRepository
import net.bytebuddy.utility.RandomString
import org.jetbrains.exposed.sql.transactions.transaction


class PodongDatabaseTests {
    init {
        Database.connect(
            "jdbc:mysql://localhost:3306/podongdaeng2", driver = "com.mysql.cj.jdbc.Driver",
            user = "root", password = "1234"
        )
    }

    @Test
    fun exposedSelect() {
        transaction {
            println(BasicRepository.selectAllAssistant())
        }
    }

    @Test
    fun makeRunRequests() {
        repeat(20) {
            transaction {
                BasicRepository.insertRunRequests(
                    runIdInput = RandomString.make(15),
                    threadIdInput = RandomString.make(15),
                    apiTypeInput = ApiRequestTypeEnum.DIET_ADVISOR,
                )
            }
        }
    }
}
