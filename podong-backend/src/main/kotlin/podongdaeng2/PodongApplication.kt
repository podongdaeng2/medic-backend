package podongdaeng2

import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.jetbrains.exposed.sql.Database
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication
@ImportAutoConfiguration(
	value = [ExposedAutoConfiguration::class],
	exclude = [DataSourceTransactionManagerAutoConfiguration::class]
)
class PodongApplication {

}

fun main(args: Array<String>) {
	runApplication<PodongApplication>(*args)
}
