package org.metplus.curriculum.process;

import org.apache.log4j.Logger;
import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.expressionCruncher.ExpressionCruncher;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by joaopereira on 2/1/2016.
 */
public class CrunchResume implements Runnable{
    private static final Logger LOG = Logger.getLogger(CrunchResume.class);
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private SettingsRepository repository;
    @Autowired
    private ExpressionCruncher expressionCruncher;
    @Autowired
    private SpringMongoConfig dbConfig;

    /**
     * Function used to Crunch all the resumes
     */
    public void crunch() {
        crunch(expressionCruncher.getCruncher());
    }

    /**
     * Function used to crunch all resumes using a specific cruncher
     * @param cruncher Cruncher to use
     */
    protected void crunch(Cruncher cruncher) {
        try {
            for(Resume resume: resumeRepository.findAll()) {
                if(!resume.isCruncherDataAvailable("expressionCruncher")){
                    cruncher.calculate(resume.getResume(dbConfig).toString());
                }
            }
        } catch (ResumeNotFound resumeNotFound) {
            resumeNotFound.printStackTrace();
            LOG.error("Resume not found:" + resumeNotFound.getMessage());
        } catch (ResumeReadException e) {
            e.printStackTrace();
            LOG.error("Error reading resume:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        crunch();
    }
}
