package org.metplus.curriculum.web.controllers;


import org.apache.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.metplus.curriculum.domain.job.CreateJob;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(value = {
        BaseController.baseUrlApiv1 + "/job/",
        BaseController.baseUrlApiv2 + "/job/",
        BaseController.baseUrlApivTesting + "/job/"})
public class JobsController {
    private static Logger logger = LoggerFactory.getLogger(JobsController.class);

    private final CreateJob createJob;

    JobsController(CreateJob createJob) {
        this.createJob = createJob;
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
}
