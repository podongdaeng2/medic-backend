package podongdaeng2.exposed.repository

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import podongdaeng2.exposed.table.TestTable

object BasicRepository {

    fun basicInsert(): Int {
        val id = transaction {
            TestTable.insert {
                it[description] = "The Last Jedi"
            } get TestTable.id
        }
        return id
    }
}