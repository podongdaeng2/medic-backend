package podongdaeng2.demo

import org.jetbrains.exposed.sql.Database
import org.junit.Test
import exposed.repository.BasicRepository


class PodongDatabaseTests {
	init {
		Database.connect("jdbc:mysql://localhost:3306/podongdaeng2", driver = "com.mysql.cj.jdbc.Driver",
			user = "root", password = "1234")
	}
	@Test
	fun exposedSelect() {
		println(BasicRepository.selectAllAssistant())
	}
}
