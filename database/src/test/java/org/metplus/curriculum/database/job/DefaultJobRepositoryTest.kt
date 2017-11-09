package org.metplus.curriculum.database.job

import org.junit.runner.RunWith
import org.metplus.curriculum.domain.job.JobRepository
import org.metplus.curriculum.domain.job.JobRepositoryTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class DefaultJobRepositoryTest : JobRepositoryTest() {
    @Autowired
    private lateinit var defaultJobRepository: DefaultJobRepository

    override val repository: JobRepository
        get() = defaultJobRepository
}