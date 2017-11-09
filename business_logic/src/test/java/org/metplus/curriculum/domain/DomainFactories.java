package org.metplus.curriculum.domain;

public class DomainFactories {
    public static Job buildJob() {
        Job job = new Job();
        job.setTitle("some title");
        job.setDescription("some description");
        job.setStarRating(0);
        return job;
    }
}
