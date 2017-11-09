package org.metplus.curriculum.domain;

public class Job {
    private String title;
    private String jobId;
    private String description;
    private JobCruncherData titleMetaData;
    private JobCruncherData descriptionMetaData;
    private double starRating;

    public static JobBuilder builder() {
        return new JobBuilder();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JobCruncherData getTitleMetaData() {
        return titleMetaData;
    }

    public void setTitleMetaData(JobCruncherData titleMetaData) {
        this.titleMetaData = titleMetaData;
    }

    public JobCruncherData getDescriptionMetaData() {
        return descriptionMetaData;
    }

    public void setDescriptionMetaData(JobCruncherData descriptionMetaData) {
        this.descriptionMetaData = descriptionMetaData;
    }

    public double getStarRating() {
        return starRating;
    }

    public void setStarRating(double starRating) {
        this.starRating = starRating;
    }

    public static class JobBuilder {
        Job job = new Job();

        public Job build() {
            return job;
        }

        public JobBuilder title(String title) {
            job.title = title;
            return this;
        }

        public JobBuilder id(String id) {
            job.jobId = id;
            return this;
        }

        public JobBuilder description(String description) {
            job.description = description;
            return this;
        }

        public JobBuilder starRating(int starRating) {
            job.starRating = starRating;
            return this;
        }
    }
}


