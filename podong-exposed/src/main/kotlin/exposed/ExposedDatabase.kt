import org.jetbrains.exposed.sql.Database

object ExposedDatabase {
    fun connect() {
        // Database connection configuration
        val url = "jdbc:mysql://localhost:3306/podongdaeng2"
        val driver = "com.mysql.cj.jdbc.Driver"
        val user = "root"
        val password = "1234"

        // Establish the database connection
        Database.connect(url, driver, user, password)
    }
}
