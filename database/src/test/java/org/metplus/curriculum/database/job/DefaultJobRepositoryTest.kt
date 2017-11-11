package org.metplus.curriculum.database.job

import org.junit.runner.RunWith
import org.metplus.curriculum.database.repository.JobDocumentRepository
import org.metplus.curriculum.database.repository.RepositoryPackage
import org.metplus.curriculum.domain.job.JobRepository
import org.metplus.curriculum.domain.job.JobRepositoryTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataMongoTest(includeFilters = arrayOf(ComponentScan.Filter(Component::class)))
@EnableMongoRepositories(basePackageClasses = arrayOf(RepositoryPackage::class))
class DefaultJobRepositoryTest : JobRepositoryTest() {
    @SpringBootConfiguration
    open class Config

    @Autowired
    private lateinit var jobRepository: JobDocumentRepository

    override val repository: JobRepository
        get() = DefaultJobRepository(jobRepository)
}