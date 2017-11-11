package org.metplus.curriculum.web.controllers;


import org.jetbrains.annotations.NotNull;
import org.metplus.curriculum.domain.job.CreateJob;
import org.metplus.curriculum.domain.job.Job;
import org.metplus.curriculum.domain.job.UpdateJob;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = {
    BaseController.baseUrlApiv1 + "/job/",
    BaseController.baseUrlApiv2 + "/job/",
    BaseController.baseUrlApivTesting + "/job/"})
public class JobsController {
    private static Logger logger = LoggerFactory.getLogger(JobsController.class);

    private final CreateJob createJob;
    private final UpdateJob updateJob;

    JobsController(
        CreateJob createJob,
        UpdateJob updateJob
    ) {
        this.createJob = createJob;
        this.updateJob = updateJob;
    }

    @RequestMapping(
        value = "create",
        method = RequestMethod.POST
    )
    @ResponseBody
    public ResponseEntity<GenericAnswer> create(
        @RequestParam("jobId") String id,
        @RequestParam("title") String title,
        @RequestParam("description") String description) {
        logger.trace("create(" + id + ", " + title + ", " + description + ")");
        return createJob.create(id, title, description, new CreateJob.ResultHandler<ResponseEntity<GenericAnswer>>() {
            @Override
            public ResponseEntity<GenericAnswer> fatalError(@NotNull String jobId) {
                GenericAnswer answer = new GenericAnswer();
                answer.setResultCode(ResultCodes.FATAL_ERROR);
                answer.setMessage("Fatal error while processing Job with id '" + jobId + "'");
                return ResponseEntity.ok(answer);
            }

            @Override
            public ResponseEntity<GenericAnswer> jobExists(@NotNull String jobId) {
                GenericAnswer answer = new GenericAnswer();
                answer.setResultCode(ResultCodes.JOB_ID_EXISTS);
                answer.setMessage("Job with id '" + jobId + "' already exists");
                return ResponseEntity.ok(answer);
            }

            @Override
            public ResponseEntity<GenericAnswer> success() {
                GenericAnswer answer = new GenericAnswer();
                answer.setResultCode(ResultCodes.SUCCESS);
                answer.setMessage("Job created successfully");
                return ResponseEntity.ok(answer);
            }
        });
    }

    @RequestMapping(value = {
        BaseController.baseUrlApiv1 + "/job/{jobId}/update",
        BaseController.baseUrlApiv2 + "/job/{jobId}/update",
        BaseController.baseUrlApivTesting + "/job/{jobId}/update"},
        method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<GenericAnswer> update(@PathVariable("jobId") final String jobId,
                                                @RequestParam(value = "title", required = false) String title,
                                                @RequestParam(value = "description", required = false) String description) {
        logger.trace("update(" + jobId + ", " + title + ", " + description + ")");
        GenericAnswer answer = new GenericAnswer();
        return updateJob.execute(jobId, title, description, new UpdateJob.ResultHandler<ResponseEntity<GenericAnswer>>() {
            @Override
            public ResponseEntity<GenericAnswer> doesNotExist(@NotNull String jobId) {
                logger.error("JobMongo with job id '" + jobId + "' do not exist");
                answer.setResultCode(ResultCodes.JOB_NOT_FOUND);
                answer.setMessage("JobMongo not found");
                return ResponseEntity.ok(answer);
            }

            @Override
            public ResponseEntity<GenericAnswer> success() {
                logger.debug("JobMongo updated successfully");
                answer.setResultCode(ResultCodes.SUCCESS);
                answer.setMessage("JobMongo updated successfully");
                return ResponseEntity.ok(answer);
            }

            @Override
            public ResponseEntity<GenericAnswer> fatalError(Job job, Exception exp) {
                logger.error("Unable to save the job '" + job + "' due to: " + exp.getMessage());
                answer.setResultCode(ResultCodes.FATAL_ERROR);
                answer.setMessage("Unable to save the job '" + job + "' due to: " + exp.getMessage());
                return ResponseEntity.ok(answer);
            }
        });
    }
}
