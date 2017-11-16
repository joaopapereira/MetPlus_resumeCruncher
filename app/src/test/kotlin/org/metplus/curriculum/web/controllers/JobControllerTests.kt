package org.metplus.curriculum.web.controllers

import org.assertj.core.api.Assertions
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.metplus.curriculum.domain.cruncher.MatcherList
import org.metplus.curriculum.database.domain.JobMongo
import org.metplus.curriculum.database.job.toJobMongo
import org.metplus.curriculum.database.repository.ResumeRepository
import org.metplus.curriculum.domain.buildJob
import org.metplus.curriculum.domain.job.Job
import org.metplus.curriculum.domain.job.testDoubles.JobRepositoryFake
import org.metplus.curriculum.process.JobCruncher
import org.metplus.curriculum.security.services.TokenService
import org.metplus.curriculum.web.answers.ResultCodes
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * Tests for the Job Controller
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    JobControllerTests.CreateEndpointVTest::class,
    JobControllerTests.CreateEndpointV1::class,
    JobControllerTests.CreateEndpointV2::class,
    JobControllerTests.UpdateEndpointVTest::class,
    JobControllerTests.UpdateEndpointV1::class,
    JobControllerTests.UpdateEndpointV2::class
)
class JobControllerTests {

    @WebMvcTest(controllers = arrayOf(JobsController::class))
    @AutoConfigureRestDocs("build/generated-snippets")
    abstract class DefaultJobTest : BaseControllerTest() {
        protected lateinit var versionUrl: String

        @Autowired
        lateinit var tokenService: TokenService

        @Autowired
        lateinit var mockMvc: MockMvc

        lateinit var jobRepositoryFake: JobRepositoryFake

        @MockBean
        lateinit var resumeRepository: ResumeRepository
        @MockBean
        lateinit var matcherList: MatcherList
        @MockBean
        lateinit var jobCruncher: JobCruncher

        lateinit var token: String


        @Configuration
        open inner class BeanConfiguration {
//            @Bean
//            open fun jobRepository(): JobRepository {
//                jobRepositoryFake = JobRepositoryFake()
//                return jobRepositoryFake
//            }
        }

        fun after() {
        }

        open fun before() {
            token = tokenService.generateToken("0.0.0.0")
//            jobRepositoryFake = JobRepositoryFake()
        }
    }

    abstract class CreateEndpoint : DefaultJobTest() {
        @Test
        @Throws(Exception::class)
        fun alreadyExists() {
            val job = buildJob("1")
            job.jobId = "1"
            jobRepositoryFake.save(job)

            createNewJob(
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", `is`(ResultCodes.JOB_ID_EXISTS.toString())))
                .andDo(document("job/create-already-exists",
                    requestHeaders(headerWithName("X-Auth-Token")
                        .description("Authentication token retrieved from the authentication")),
                    requestParameters(
                        parameterWithName("jobId").description("Job Identifier to create"),
                        parameterWithName("title").description("Title of the job"),
                        parameterWithName("description").description("Description of the job")
                    ),
                    responseFields(
                        fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                        fieldWithPath("message").description("Message associated with the result code")
                    )
                ))
            val savedJob = jobRepositoryFake.findById("1")
            Assertions.assertThat(job).isEqualToComparingFieldByField(savedJob)
            Mockito.verify<JobCruncher>(jobCruncher, Mockito.times(0)).addWork(Mockito.any(JobMongo::class.java))
        }

        @Test
        @Throws(Exception::class)
        fun success() {
            val job = buildJob(
                jobId = "1"
            )
            jobRepositoryFake.save(job)
            createNewJob(
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", `is`(ResultCodes.SUCCESS.toString())))
                .andDo(document("job/create-success",
                    requestHeaders(headerWithName("X-Auth-Token")
                        .description("Authentication token retrieved from the authentication")),
                    requestParameters(
                        parameterWithName("jobId").description("Job Identifier to create"),
                        parameterWithName("title").description("Title of the job"),
                        parameterWithName("description").description("Description of the job")
                    ),
                    responseFields(
                        fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                        fieldWithPath("message").description("Message associated with the result code")
                    )
                ))
            val savedJob = jobRepositoryFake.findById("1")
            Assertions.assertThat(savedJob).isEqualToComparingFieldByField(buildJob(
                jobId = "1",
                title = "Job title",
                description = "My awsome job description"
            ))

            Mockito.verify<JobCruncher>(jobCruncher).addWork(savedJob!!.toJobMongo())
        }

        @Throws(Exception::class)
        private fun createNewJob(): ResultActions {
            return mockMvc.perform(post("/api/$versionUrl/job/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("jobId", "1")
                .param("title", "Job title")
                .param("description", "My awsome job description")
                .header("X-Auth-Token", token)
            )
        }
    }

    @RunWith(SpringRunner::class)
    class CreateEndpointV1 : CreateEndpoint() {
        @Before
        override fun before() {
            super.before()
            this.versionUrl = "v1"
        }
    }

    @RunWith(SpringRunner::class)
    class CreateEndpointV2 : CreateEndpoint() {
        @Before
        override fun before() {
            super.before()
            this.versionUrl = "v2"
        }
    }

    @RunWith(SpringRunner::class)
    class CreateEndpointVTest : CreateEndpoint() {
        @Before
        override fun before() {
            super.before()
            this.versionUrl = "v99999"
        }
    }

    abstract class UpdateEndpoint : DefaultJobTest() {
        @Test
        @Throws(Exception::class)
        fun doNotExist() {

            updateJob(
                "Job title", "My awsome job description")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", `is`(ResultCodes.JOB_NOT_FOUND.toString())))
                .andDo(document("job/update-not-exists",
                    requestHeaders(headerWithName("X-Auth-Token")
                        .description("Authentication token retrieved from the authentication")),
                    pathParameters(parameterWithName("jobId").description("Job Identifier to create")),
                    requestParameters(
                        parameterWithName("title").description("Title of the job"),
                        parameterWithName("description").description("Description of the job")
                    ),
                    responseFields(
                        fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                        fieldWithPath("message").description("Message associated with the result code")
                    )
                ))

            Mockito.verify<JobCruncher>(jobCruncher, Mockito.times(0)).addWork(Mockito.any())
        }

        @Test
        @Throws(Exception::class)
        fun success() {
            val job = jobRepositoryFake.save(Job(
                jobId = "1",
                title = "My current title",
                description = "My current description"
            ))

            updateJob(
                "Job title",
                "My awesome job description")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", `is`(ResultCodes.SUCCESS.toString())))
                .andDo(document("job/update-success",
                    requestHeaders(headerWithName("X-Auth-Token")
                        .description("Authentication token retrieved from the authentication")),
                    pathParameters(parameterWithName("jobId").description("Job Identifier to create")),
                    requestParameters(
                        parameterWithName("title").description("Title of the job(Optional)"),
                        parameterWithName("description").description("Description of the job(Optional)")
                    ),
                    responseFields(
                        fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                        fieldWithPath("message").description("Message associated with the result code")
                    )
                ))

            val savedJob = jobRepositoryFake.findById("1")
            Assertions.assertThat(savedJob).isEqualToComparingFieldByField(buildJob(
                jobId = "1",
                title = "Job title",
                description = "My awesome job description"
            ))
            Mockito.verify<JobCruncher>(jobCruncher).addWork(savedJob!!.toJobMongo())
        }

        @Test
        @Throws(Exception::class)
        fun successOnlyTitle() {
            val job = jobRepositoryFake.save(Job(
                jobId = "1",
                title = "My current title",
                description = "My current description"
            ))

            updateJob(
                "Job title", null)
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", `is`(ResultCodes.SUCCESS.toString())))

            val savedJob = jobRepositoryFake.findById("1")
            Assertions.assertThat(savedJob).isEqualToIgnoringGivenFields(job, "title")
            Assertions.assertThat(savedJob!!.title).isEqualTo("Job title")
            Mockito.verify<JobCruncher>(jobCruncher).addWork(savedJob.toJobMongo())
        }

        @Test
        @Throws(Exception::class)
        fun successOnlyDescription() {
            val job = jobRepositoryFake.save(Job(
                jobId = "1",
                title = "My current title",
                description = "My current description"
            ))

            updateJob(null,
                "My awesome job description")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", `is`(ResultCodes.SUCCESS.toString())))

            val savedJob = jobRepositoryFake.findById("1")
            Assertions.assertThat(savedJob).isEqualToIgnoringGivenFields(job, "description")
            Assertions.assertThat(savedJob!!.description).isEqualTo("My awesome job description")
            Mockito.verify<JobCruncher>(jobCruncher).addWork(savedJob.toJobMongo())
        }

        @Throws(Exception::class)
        private fun updateJob(jobTitle: String?, jobDescription: String?): ResultActions {
            return mockMvc.perform(patch("/api/$versionUrl/job/{jobId}/update", "1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", jobTitle!!)
                .param("description", jobDescription!!)
                .header("X-Auth-Token", token)
            )
        }
    }


    @RunWith(SpringRunner::class)
    class UpdateEndpointV1 : UpdateEndpoint() {
        override fun before() {
            super.before()
            this.versionUrl = "v1"
        }
    }


    @RunWith(SpringRunner::class)
    class UpdateEndpointV2 : UpdateEndpoint() {
        override fun before() {
            super.before()
            this.versionUrl = "v2"
        }
    }


    @RunWith(SpringRunner::class)
    class UpdateEndpointVTest : UpdateEndpoint() {
        override fun before() {
            super.before()
            this.versionUrl = "v99999"
        }
    }
}
