package org.metplus.curriculum.process;

import org.metplus.curriculum.domain.cruncher.Cruncher;
import org.metplus.curriculum.database.domain.DocumentWithMetaData;
import org.metplus.curriculum.database.domain.JobMongo;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.repository.JobDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Process that will be running in the background
 * to crunch the job information
 */
@Component("jobCruncher")
public class JobCruncher extends ProcessCruncher<JobMongo>{
    private static final Logger logger = LoggerFactory.getLogger(JobCruncher.class);

    @Autowired
    JobDocumentRepository jobRepository;

    @Override
    protected void process(JobMongo job) {
        logger.trace("process({})", job);
        Map<String, MetaData> allDescriptionMetaData = new HashMap<>();
        Map<String, MetaData> allTitleMetaData = new HashMap<>();
        for(Cruncher cruncher: allCrunchers.getCrunchers()) {
            MetaData titleMetaData = new MetaData();
            try {
                titleMetaData = (MetaData) cruncher.crunch(job.getTitle());
            }catch(Exception exp) {
                logger.warn("Error crunching the title of job: " + job.getJobId() + ": " + exp);
                exp.printStackTrace();
            }
            MetaData descriptionMetaData = new MetaData();
            try {
                descriptionMetaData = (MetaData) cruncher.crunch(job.getDescription());
            }catch(Exception exp) {
                logger.warn("Error crunching the description of job: " + job.getJobId() + ": " + exp);
                exp.printStackTrace();
            }
            allTitleMetaData.put(cruncher.getCruncherName(), titleMetaData);
            allDescriptionMetaData.put(cruncher.getCruncherName(), descriptionMetaData);
        }
        DocumentWithMetaData titleData = new DocumentWithMetaData();
        titleData.setMetaData(allTitleMetaData);
        job.setTitleMetaData(titleData);
        DocumentWithMetaData descriptionData = new DocumentWithMetaData();
        descriptionData.setMetaData(allDescriptionMetaData);
        job.setDescriptionMetaData(descriptionData);
        jobRepository.save(job);
        logger.debug("JobMongo [{}] processed successfully", job);
    }
}
