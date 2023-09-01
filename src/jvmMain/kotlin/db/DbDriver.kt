package db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

object DbDriver {
    val dbDriver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:applications.db")
//    fun createSchema() {
//        applications.Schema.create(dbDriver)
//    }
}
