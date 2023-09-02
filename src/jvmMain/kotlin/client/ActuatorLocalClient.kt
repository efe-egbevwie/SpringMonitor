package client

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.efe.applications
import comefe.ApplicationQueries
import comefe.Applications
import common.domain.Application
import java.io.File

object ActuatorLocalClient {

    private val dbDriver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:applications.db")

    init {
        if (!File("applications.db").exists()) {
            applications.Schema.create(dbDriver)
        }

        val filePath = File("applications.db").absolutePath
        println("file path: $filePath")

    }

    private val applicationQueries = ApplicationQueries(dbDriver)

    val getAllActuators =
        applicationQueries.selectAll()
            .asFlow()


    fun insertApplication(application: Application) {
        applicationQueries.insertApplication(
            application_alias = application.alias,
            actuator_url = application.actuatorUrl,
            bearer_token = application.bearerToken
        )
    }

    fun findApplicationById(applicationId: Int) {
        val application: List<Applications> =
            applicationQueries.findApplicationById(applicationId.toLong()).executeAsList()
    }

    fun deleteAllApps() {

    }


}