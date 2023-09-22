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

object ApplicationsDb {

    private val dbDriver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:applications.db")

    private val logger = KotlinLogging.logger { }

    init {
        if (!File("applications.db").exists()) {
            applications.Schema.create(dbDriver)
        }

        val filePath = File("applications.db").absolutePath
        println("file path: $filePath")

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