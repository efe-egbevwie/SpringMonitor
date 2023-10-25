package client

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.efe.applications
import comefe.ApplicationQueries
import domain.models.Application
import domain.models.toDomainApplication
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import kotlin.io.path.Path

object ApplicationsDb {

    private val databasePath: String

    private val dbDriver: SqlDriver

    private val logger = KotlinLogging.logger { }

    init {
        val userHome = System.getProperty("user.home")

        val databaseDirectory = Path(userHome).resolve(".SpringMonitor")
        databaseDirectory.toFile().mkdirs()

        databasePath = databaseDirectory.resolve("applications.db").toString()
        dbDriver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        if (!File(databasePath).exists()) {
            applications.Schema.create(dbDriver)
        }

        logger.info { "file path: $databasePath" }

    }

    private val applicationQueries = ApplicationQueries(dbDriver)

    val getAllApplications: Flow<List<Application>> =
        applicationQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { applicationsEntities ->
                applicationsEntities.map {
                    it.toDomainApplication()
                }
            }


    fun insertApplication(application: Application) {
        applicationQueries.insertApplication(
            application_alias = application.alias,
            actuator_url = application.actuatorUrl,
            bearer_token = application.bearerToken
        )
    }

    fun findApplicationById(applicationId: Int): Flow<Application?> {
        return applicationQueries.findApplicationById(applicationId.toLong()).asFlow().map { applicationEntity ->
            applicationEntity.executeAsOneOrNull()?.toDomainApplication()
        }

    }

    fun updateApplication(application: Application) {

        if (application.applicationId == null) return

        applicationQueries.updateApplication(
            application_alias = application.alias,
            actuator_url = application.actuatorUrl,
            bearer_token = application.bearerToken,
            application_id = application.applicationId.toLong()
        )
    }

    fun deleteApplication(applicationId: Int?) {
        if (applicationId == null) return
        applicationQueries.deleteById(applicationId.toLong())
    }


}