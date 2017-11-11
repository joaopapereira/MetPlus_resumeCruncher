package org.metplus.curriculum.domain.job

import org.metplus.curriculum.domain.job.testDoubles.JobRepositoryFake

class JobRepositoryFakeTest : JobRepositoryTest() {
    override val repository
        get() = JobRepositoryFake()
}