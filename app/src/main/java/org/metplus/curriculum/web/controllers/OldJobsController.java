package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.database.domain.JobMongo;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobDocumentRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.process.JobCruncher;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.metplus.curriculum.web.answers.JobMatchAnswer;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller that will handle
 * all the requests related with jobs
 */
@RestController
public class OldJobsController {
    private static Logger logger = LoggerFactory.getLogger(OldJobsController.class);
    @Autowired
    private JobDocumentRepository jobRepository;
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private MatcherList matcherList;
    @Autowired
    private JobCruncher jobCruncher;

    public OldJobsController() {
    }


    public OldJobsController(JobDocumentRepository jobRepository, ResumeRepository resumeRepository, MatcherList matcherList, JobCruncher jobCruncher) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.matcherList = matcherList;
        this.jobCruncher = jobCruncher;
    }

    @RequestMapping(value = {BaseController.baseUrlApiv1 + "/job/reindex",
            BaseController.baseUrlApiv2 + "/job/reindex",
            BaseController.baseUrlApivTesting + "/job/reindex"},
            method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericAnswer> reindex() {
        logger.debug("reindex()");
        GenericAnswer answer = new GenericAnswer();
        int total = 0;
        for (JobMongo job : jobRepository.findAll()) {
            jobCruncher.addWork(job);
            total++;
        }
        answer.setMessage("Going to reindex " + total + " jobs");
        answer.setResultCode(ResultCodes.SUCCESS);

        logger.debug("Result is: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = BaseController.baseUrlApiv2 + "/job/match/{resumeId}",
            method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchv2(@PathVariable("resumeId") final String resumeId) {
        return match(resumeId, true);
    }

    @RequestMapping(value = BaseController.baseUrlApiv1 + "/job/match/{resumeId}",
            method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchv1(@PathVariable("resumeId") final String resumeId) {
        return match(resumeId, false);
    }

    @RequestMapping(value = BaseController.baseUrlApivTesting + "/job/match/{resumeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchCannedAnswer(@PathVariable("resumeId") final String resumeId) {

        final double[] jobStars = {1.8, 4.1, 2.6, 4.9, 3.2,
                1.8, 4.1, 2.6, 4.9, 3.2};
        JobMatchAnswer answer = new JobMatchAnswer();
        double jobIdentifiers = Double.valueOf(resumeId);
        if (jobIdentifiers > 0 && jobIdentifiers < 11) {
            for (int i = 0; i < jobStars.length; i++) {
                JobMongo job = jobRepository.findByJobId(Integer.toString(i));
                if (job != null) {
                    job.setStarRating(jobStars[i]);
                    answer.addJob("NaiveBayes", job, true);
                }
            }
        } else if (jobIdentifiers % 5 != 0) {
            for (int i = 0; i < 4; i++) {
                JobMongo job = jobRepository.findByJobId(Double.toString(jobIdentifiers + i));
                if (job != null) {
                    job.setStarRating(jobStars[i]);
                    answer.addJob("NaiveBayes", job, true);
                }
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }


    private ResponseEntity<GenericAnswer> match(String resumeId, boolean withProbabilityAnswer) {
        logger.debug("match(" + resumeId + ", " + withProbabilityAnswer + ")");
        Resume resume = resumeRepository.findByUserId(resumeId);
        if (resume == null) {
            logger.warn("Unable to find resume with id '{}'", resumeId);
            GenericAnswer answer = new GenericAnswer();
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
            answer.setMessage("Cannot find the resume");
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }
        logger.debug("Start processing the Jobs");
        List<JobMongo> matchedJobs = null;
        JobMatchAnswer answer = null;
        if (withProbabilityAnswer)
            answer = new JobMatchAnswer<JobMatchAnswer.JobWithProbability>();
        else
            answer = new JobMatchAnswer<String>();
        for (Matcher matcher : matcherList.getMatchers()) {
            matchedJobs = matcher.match(resume);
            if (matchedJobs == null) {
                logger.error("Unable to find to jobs because the resume with id '{}' is in a invalid state", resumeId);
                GenericAnswer errorAnswer = new GenericAnswer();
                errorAnswer.setResultCode(ResultCodes.FATAL_ERROR);
                errorAnswer.setMessage("Unable to find to jobs because the resume is in a invalid state");
                return new ResponseEntity<>(answer, HttpStatus.OK);
            }
            for (JobMongo job : matchedJobs) {
                answer.addJob(matcher.getCruncherName(), job, withProbabilityAnswer);
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);
        logger.debug("Done processing: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
