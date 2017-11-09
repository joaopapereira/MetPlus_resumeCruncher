package org.metplus.curriculum.domain.job;

public class JobRepositoryFakeTest extends JobRepositoryTest {
    @Override
    protected JobRepository getRepository() {
        return new JobRepositoryFake();
    }
}