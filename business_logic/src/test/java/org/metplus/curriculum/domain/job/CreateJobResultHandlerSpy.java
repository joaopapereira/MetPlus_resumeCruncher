package org.metplus.curriculum.domain.job;

public class CreateJobResultHandlerSpy implements CreateJob.ResultHandler<Void> {
    public String jobExistsWasCalledWith = null;
    public boolean successWasCalled = false;
    public boolean fatalErrorWasCalled = false;

    @Override
    public Void jobExists(String jobId) {
        jobExistsWasCalledWith = jobId;
        return null;
    }

    @Override
    public Void fatalError() {
        fatalErrorWasCalled = true;
        return null;
    }

    @Override
    public Void success() {
        successWasCalled = true;
        return null;
    }
}
