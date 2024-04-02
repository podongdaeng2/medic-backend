package podongdaeng2.exposed.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TestTable : Table("TestTable") {
    val id: Column<Int> = integer("id").autoIncrement()
    val description: Column<String> = varchar("description", 20)
    override val primaryKey = PrimaryKey(id, name = "PK_TestTable_Id")
}