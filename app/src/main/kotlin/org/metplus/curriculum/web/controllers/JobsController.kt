package org.metplus.curriculum.web.controllers


import org.metplus.curriculum.database.job.toJobMongo
import org.metplus.curriculum.domain.job.CreateJob
import org.metplus.curriculum.domain.job.Job
import org.metplus.curriculum.domain.job.UpdateJob
import org.metplus.curriculum.process.JobCruncher
import org.metplus.curriculum.web.answers.GenericAnswer
import org.metplus.curriculum.web.answers.ResultCodes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping(value = *arrayOf(BaseController.baseUrlApiv1 + "/job/", BaseController.baseUrlApiv2 + "/job/", BaseController.baseUrlApivTesting + "/job/"))
class JobsController internal constructor(
    private val createJob: CreateJob,
    private val updateJob: UpdateJob,
    private val jobCruncher: JobCruncher
) {

    @RequestMapping(value = "create", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun create(
        @RequestParam("jobId") id: String,
        @RequestParam("title") title: String,
        @RequestParam("description") description: String): ResponseEntity<GenericAnswer> {
        logger.trace("create($id, $title, $description)")
        return createJob.create(id, title, description, object : CreateJob.ResultHandler<ResponseEntity<GenericAnswer>> {
            override fun fatalError(jobId: String): ResponseEntity<GenericAnswer> {
                val answer = GenericAnswer()
                answer.resultCode = ResultCodes.FATAL_ERROR
                answer.message = "Fatal error while processing Job with id '$jobId'"
                return ResponseEntity.ok(answer)
            }

            override fun jobExists(jobId: String): ResponseEntity<GenericAnswer> {
                val answer = GenericAnswer()
                answer.resultCode = ResultCodes.JOB_ID_EXISTS
                answer.message = "Job with id '$jobId' already exists"
                return ResponseEntity.ok(answer)
            }

            override fun success(job: Job): ResponseEntity<GenericAnswer> {
                jobCruncher.addWork(job.toJobMongo())
                val answer = GenericAnswer()
                answer.resultCode = ResultCodes.SUCCESS
                answer.message = "Job created successfully"
                return ResponseEntity.ok(answer)
            }
        })
    }

    @RequestMapping(value = *arrayOf(BaseController.baseUrlApiv1 + "/job/{jobId}/update", BaseController.baseUrlApiv2 + "/job/{jobId}/update", BaseController.baseUrlApivTesting + "/job/{jobId}/update"), method = arrayOf(RequestMethod.PATCH))
    @ResponseBody
    fun update(@PathVariable("jobId") jobId: String,
               @RequestParam(value = "title", required = false) title: String,
               @RequestParam(value = "description", required = false) description: String): ResponseEntity<GenericAnswer> {
        logger.trace("update($jobId, $title, $description)")
        val answer = GenericAnswer()
        return updateJob.execute(jobId, title, description, object : UpdateJob.ResultHandler<ResponseEntity<GenericAnswer>> {
            override fun doesNotExist(jobId: String): ResponseEntity<GenericAnswer> {
                logger.error("JobMongo with job id '$jobId' do not exist")
                answer.resultCode = ResultCodes.JOB_NOT_FOUND
                answer.message = "JobMongo not found"
                return ResponseEntity.ok(answer)
            }

            override fun success(job: Job): ResponseEntity<GenericAnswer> {
                jobCruncher.addWork(job.toJobMongo())
                logger.debug("JobMongo updated successfully")
                answer.resultCode = ResultCodes.SUCCESS
                answer.message = "JobMongo updated successfully"
                return ResponseEntity.ok(answer)
            }

            override fun fatalError(job: Job, exp: Exception): ResponseEntity<GenericAnswer> {
                logger.error("Unable to save the job '" + job + "' due to: " + exp.message)
                answer.resultCode = ResultCodes.FATAL_ERROR
                answer.message = "Unable to save the job '" + job + "' due to: " + exp.message
                return ResponseEntity.ok(answer)
            }
        })
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobsController::class.java)
    }
}
