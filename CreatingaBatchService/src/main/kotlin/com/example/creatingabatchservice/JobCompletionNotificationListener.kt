package com.example.creatingabatchservice

import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class JobCompletionNotificationListener(
    private val jdbcTemplate: JdbcTemplate
): JobExecutionListener {

    val log = logger()

    override fun afterJob(jobExecution: JobExecution) {
        if (jobExecution.status == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results")

            jdbcTemplate
                .query("SELECT first_name, last_name FROM people", DataClassRowMapper<Person>(Person::class.java))
                .forEach { person: Person? -> log.info("Found <$person> in the database.") }
        }
    }
}
