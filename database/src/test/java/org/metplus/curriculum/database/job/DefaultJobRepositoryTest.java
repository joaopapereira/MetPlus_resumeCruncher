package org.metplus.curriculum.database.job;

import org.junit.runner.RunWith;
import org.metplus.curriculum.domain.job.JobRepository;
import org.metplus.curriculum.domain.job.JobRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultJobRepositoryTest extends JobRepositoryTest {
    @Autowired
    private DefaultJobRepository defaultJobRepository;

    @Override
    protected JobRepository getRepository() {
        return defaultJobRepository;
    }
}