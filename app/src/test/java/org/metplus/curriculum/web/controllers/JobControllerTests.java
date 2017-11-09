package org.metplus.curriculum.web.controllers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.database.domain.JobMongo;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobDocumentRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.process.JobCruncher;
import org.metplus.curriculum.security.services.TokenService;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the JobMongo Controller
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        JobControllerTests.CreateEndpointVTest.class,
        JobControllerTests.CreateEndpointV1.class,
        JobControllerTests.CreateEndpointV2.class,
        JobControllerTests.UpdateEndpointVTest.class,
        JobControllerTests.UpdateEndpointV1.class,
        JobControllerTests.UpdateEndpointV2.class,
        JobControllerTests.MatchEndpointV1.class,
        JobControllerTests.MatchEndpointV2.class})
public class JobControllerTests {

    @WebMvcTest(controllers = OldJobsController.class)
    @AutoConfigureRestDocs("build/generated-snippets")
    public static class DefaultJobTest extends BaseControllerTest implements BeforeAfterInterface {
        protected String versionUrl;

        @Autowired
        TokenService tokenService;

        @Autowired
        MockMvc mockMvc;

        @MockBean
        JobDocumentRepository jobRepository;
        @MockBean
        ResumeRepository resumeRepository;
        @MockBean
        MatcherList matcherList;
        @MockBean
        JobCruncher jobCruncher;

        String token;

        @Rule
        public BeforeAfterRule rule = new BeforeAfterRule(this);

        @Override
        public void after() {

        }

        @Override
        public void before() {
            token = tokenService.generateToken("0.0.0.0");
        }
    }

    public static abstract class CreateEndpoint extends DefaultJobTest {
        @Test
        public void alreadyExists() throws Exception {
            JobMongo job = new JobMongo();
            job.setJobId("1");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            createNewJob(
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.JOB_ID_EXISTS.toString())))
                    .andDo(document("job/create-already-exists",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(
                                    parameterWithName("jobId").description("JobMongo Identifier to create"),
                                    parameterWithName("title").description("Title of the job"),
                                    parameterWithName("description").description("Description of the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(jobRepository).findByJobId("1");
            Mockito.verify(jobRepository, Mockito.times(0)).save(Mockito.any(JobMongo.class));
            Mockito.verify(jobCruncher, Mockito.times(0)).addWork(Mockito.any(JobMongo.class));
        }

        @Test
        public void success() throws Exception {
            JobMongo job = new JobMongo();
            job.setJobId("1");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(null);

            createNewJob(
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andDo(document("job/create-success",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(
                                    parameterWithName("jobId").description("JobMongo Identifier to create"),
                                    parameterWithName("title").description("Title of the job"),
                                    parameterWithName("description").description("Description of the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(jobRepository).findByJobId("1");
            ArgumentCaptor<JobMongo> jobArgumentCaptor = ArgumentCaptor.forClass(JobMongo.class);
            Mockito.verify(jobRepository).save(jobArgumentCaptor.capture());
            assertEquals("JobMongo title", jobArgumentCaptor.getValue().getTitle());
            assertEquals("1", jobArgumentCaptor.getValue().getJobId());
            assertEquals("My awsome job description", jobArgumentCaptor.getValue().getDescription());
            ArgumentCaptor<JobMongo> allJobs = ArgumentCaptor.forClass(JobMongo.class);
            Mockito.verify(jobRepository).save(allJobs.capture());
            Mockito.verify(jobCruncher).addWork(allJobs.getValue());
        }

        private ResultActions createNewJob() throws Exception {
            return mockMvc.perform(post("/api/" + versionUrl + "/job/create")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("jobId", "1")
                    .param("title", "JobMongo title")
                    .param("description", "My awsome job description")
                    .header("X-Auth-Token", token)
            );
        }
    }

    @RunWith(SpringRunner.class)
    public static class CreateEndpointV1 extends CreateEndpoint {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "v1";
        }
    }

    @RunWith(SpringRunner.class)
    public static class CreateEndpointV2 extends CreateEndpoint {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "v2";
        }
    }

    @RunWith(SpringRunner.class)
    public static class CreateEndpointVTest extends CreateEndpoint {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "v99999";
        }
    }

    public static abstract class UpdateEndpoint extends DefaultJobTest {
        @Test
        public void doNotExist() throws Exception {
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(null);

            updateJob(
                    "JobMongo title", "My awsome job description")
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.JOB_NOT_FOUND.toString())))
                    .andDo(document("job/update-not-exists",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("jobId").description("JobMongo Identifier to create")),
                            requestParameters(
                                    parameterWithName("title").description("Title of the job"),
                                    parameterWithName("description").description("Description of the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(jobRepository).findByJobId("1");
            Mockito.verify(jobRepository, Mockito.times(0)).save((JobMongo) Mockito.any());
            Mockito.verify(jobRepository, Mockito.times(0)).save(Mockito.any(JobMongo.class));
            Mockito.verify(jobCruncher, Mockito.times(0)).addWork(Mockito.any(JobMongo.class));
        }

        @Test
        public void success() throws Exception {
            JobMongo job = new JobMongo();
            job.setJobId("1");
            job.setTitle("My current title");
            job.setDescription("My current description");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            updateJob(
                    "JobMongo title",
                    "My awsome job description")
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andDo(document("job/update-success",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("jobId").description("JobMongo Identifier to create")),
                            requestParameters(
                                    parameterWithName("title").description("Title of the job(Optional)"),
                                    parameterWithName("description").description("Description of the job(Optional)")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(jobRepository).findByJobId("1");
            ArgumentCaptor<JobMongo> jobArgumentCaptor = ArgumentCaptor.forClass(JobMongo.class);
            Mockito.verify(jobRepository).save(jobArgumentCaptor.capture());
            assertEquals("JobMongo title", jobArgumentCaptor.getValue().getTitle());
            assertEquals("1", jobArgumentCaptor.getValue().getJobId());
            assertEquals("My awsome job description", jobArgumentCaptor.getValue().getDescription());
            ArgumentCaptor<JobMongo> allJobs = ArgumentCaptor.forClass(JobMongo.class);
            Mockito.verify(jobRepository).save(allJobs.capture());
            Mockito.verify(jobCruncher).addWork(allJobs.getValue());
        }

        @Test
        public void successOnlyTitle() throws Exception {
            JobMongo job = new JobMongo();
            job.setJobId("1");
            job.setTitle("My current title");
            job.setDescription("My current description");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            updateJob(
                    "JobMongo title",
                    null)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())));
            Mockito.verify(jobRepository).findByJobId("1");
            ArgumentCaptor<JobMongo> jobArgumentCaptor = ArgumentCaptor.forClass(JobMongo.class);
            Mockito.verify(jobRepository).save(jobArgumentCaptor.capture());
            assertEquals("JobMongo title", jobArgumentCaptor.getValue().getTitle());
            assertEquals("1", jobArgumentCaptor.getValue().getJobId());
            assertEquals("My current description", jobArgumentCaptor.getValue().getDescription());
            ArgumentCaptor<JobMongo> allJobs = ArgumentCaptor.forClass(JobMongo.class);
            Mockito.verify(jobRepository).save(allJobs.capture());
            Mockito.verify(jobCruncher).addWork(allJobs.getValue());
        }

        @Test
        public void successOnlyDescription() throws Exception {
            JobMongo job = new JobMongo();
            job.setJobId("1");
            job.setTitle("My current title");
            job.setDescription("My current description");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            updateJob(
                    null,
                    "My awsome job description")
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())));
            Mockito.verify(jobRepository).findByJobId("1");
            ArgumentCaptor<JobMongo> jobArgumentCaptor = ArgumentCaptor.forClass(JobMongo.class);
            Mockito.verify(jobRepository).save(jobArgumentCaptor.capture());
            assertEquals("My current title", jobArgumentCaptor.getValue().getTitle());
            assertEquals("1", jobArgumentCaptor.getValue().getJobId());
            assertEquals("My awsome job description", jobArgumentCaptor.getValue().getDescription());
            ArgumentCaptor<JobMongo> allJobs = ArgumentCaptor.forClass(JobMongo.class);
            Mockito.verify(jobRepository).save(allJobs.capture());
            Mockito.verify(jobCruncher).addWork(allJobs.getValue());
        }

        private ResultActions updateJob(String jobTitle, String jobDescription) throws Exception {
            return mockMvc.perform(patch("/api/" + versionUrl + "/job/{jobId}/update", "1")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("title", jobTitle)
                    .param("description", jobDescription)
                    .header("X-Auth-Token", token)
            );
        }
    }


    @RunWith(SpringRunner.class)
    public static class UpdateEndpointV1 extends UpdateEndpoint {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "v1";
        }
    }


    @RunWith(SpringRunner.class)
    public static class UpdateEndpointV2 extends UpdateEndpoint {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "v2";
        }
    }


    @RunWith(SpringRunner.class)
    public static class UpdateEndpointVTest extends UpdateEndpoint {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "v99999";
        }
    }

    @RunWith(SpringRunner.class)
    public static class MatchEndpointV1 extends DefaultJobTest {
        @Test
        public void doNotExist() throws Exception {
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(null);

            matchWithResume(
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.RESUME_NOT_FOUND.toString())))
                    .andDo(document("job/match-not-exists/v1",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
        }

        @Test
        public void success() throws Exception {
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            Resume resume = new Resume("1");
            resume.setMetaData(metaData1);
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(resume);

            JobMongo job1 = new JobMongo();
            job1.setJobId("1");
            job1.setStarRating(4.2);
            JobMongo job2 = new JobMongo();
            job2.setStarRating(1.0);
            job2.setJobId("2");
            JobMongo job3 = new JobMongo();
            job3.setJobId("3");
            job3.setStarRating(3.);
            JobMongo job4 = new JobMongo();
            job4.setJobId("2");
            job4.setStarRating(0.5);
            List<JobMongo> matcher1Resumes = new ArrayList<>();
            matcher1Resumes.add(job1);
            matcher1Resumes.add(job2);
            List<JobMongo> matcher2Resumes = new ArrayList<>();
            matcher2Resumes.add(job3);
            matcher2Resumes.add(job4);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(CruncherMetaData.class))).thenReturn(matcher1Resumes);
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(CruncherMetaData.class))).thenReturn(matcher2Resumes);

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);


            matchWithResume(
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.jobs.matcher1", hasSize(2)))
                    .andExpect(jsonPath("$.jobs.matcher1[0]", is("1")))
                    .andExpect(jsonPath("$.jobs.matcher1[1]", is("2")))
                    .andExpect(jsonPath("$.jobs.matcher2", hasSize(2)))
                    .andExpect(jsonPath("$.jobs.matcher2[0]", is("3")))
                    .andExpect(jsonPath("$.jobs.matcher2[1]", is("2")))
                    .andDo(document("job/match-success/v1",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("jobs").description("Hash with a list of job IDs matched by each cruncher")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
            Mockito.verify(matcher1).match(Mockito.any(CruncherMetaData.class));
            Mockito.verify(matcher2).match(Mockito.any(CruncherMetaData.class));
        }

        @Test
        public void noMatches() throws Exception {
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            Resume resume = new Resume("1");
            resume.setMetaData(metaData1);
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(resume);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(CruncherMetaData.class))).thenReturn(new ArrayList<>());
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(CruncherMetaData.class))).thenReturn(new ArrayList<>());

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);


            matchWithResume(
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.jobs").value(is(new java.util.LinkedHashMap())))
                    .andDo(document("job/match-not-found/v1",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("jobs").description("Hash with a list of job ids matched by each cruncher")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
            Mockito.verify(matcher1).match(Mockito.any(CruncherMetaData.class));
            Mockito.verify(matcher2).match(Mockito.any(CruncherMetaData.class));
        }

        private ResultActions matchWithResume() throws Exception {
            return mockMvc.perform(
                    get("/api/v1/job/match/{resumeId}", "1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("X-Auth-Token", token)
            );
        }
    }


    @RunWith(SpringRunner.class)
    public static class MatchEndpointV2 extends DefaultJobTest {
        @Test
        public void doNotExist() throws Exception {
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(null);

            matchResume()
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.RESUME_NOT_FOUND.toString())))
                    .andDo(document("job/match-not-exists/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
        }

        @Test
        public void success() throws Exception {
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            Resume resume = new Resume("1");
            resume.setMetaData(metaData1);
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(resume);

            JobMongo job1 = new JobMongo();
            job1.setJobId("1");
            job1.setStarRating(4.23);
            JobMongo job2 = new JobMongo();
            job2.setStarRating(1.0);
            job2.setJobId("2");
            JobMongo job3 = new JobMongo();
            job3.setJobId("3");
            job3.setStarRating(3.);
            JobMongo job4 = new JobMongo();
            job4.setJobId("2");
            job4.setStarRating(0.51);
            List<JobMongo> matcher1Resumes = new ArrayList<>();
            matcher1Resumes.add(job1);
            matcher1Resumes.add(job2);
            List<JobMongo> matcher2Resumes = new ArrayList<>();
            matcher2Resumes.add(job3);
            matcher2Resumes.add(job4);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(CruncherMetaData.class))).thenReturn(matcher1Resumes);
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(CruncherMetaData.class))).thenReturn(matcher2Resumes);

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);


            matchResume()
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.jobs.matcher1", hasSize(2)))
                    .andExpect(jsonPath("$.jobs.matcher1[0].jobId", is("1")))
                    .andExpect(jsonPath("$.jobs.matcher1[0].stars", is(4.2)))
                    .andExpect(jsonPath("$.jobs.matcher1[1].jobId", is("2")))
                    .andExpect(jsonPath("$.jobs.matcher1[1].stars", is(1.0)))
                    .andExpect(jsonPath("$.jobs.matcher2", hasSize(2)))
                    .andExpect(jsonPath("$.jobs.matcher2[0].jobId", is("3")))
                    .andExpect(jsonPath("$.jobs.matcher2[0].stars", is(3.)))
                    .andExpect(jsonPath("$.jobs.matcher2[1].jobId", is("2")))
                    .andExpect(jsonPath("$.jobs.matcher2[1].stars", is(.5)))
                    .andDo(document("job/match-success/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("jobs").description("Hash with a list of job IDs and the star rating matched by each cruncher")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
            Mockito.verify(matcher1).match(Mockito.any(CruncherMetaData.class));
            Mockito.verify(matcher2).match(Mockito.any(CruncherMetaData.class));
        }

        @Test
        public void noMatches() throws Exception {
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            Resume resume = new Resume("1");
            resume.setMetaData(metaData1);
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(resume);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(CruncherMetaData.class))).thenReturn(new ArrayList<>());
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(CruncherMetaData.class))).thenReturn(new ArrayList<>());

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);


            matchResume()
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.jobs").value(is(new java.util.LinkedHashMap())))
                    .andDo(document("job/match-not-found/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("jobs").description("Hash with a list of job names matched by each cruncher")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
            Mockito.verify(matcher1).match(Mockito.any(CruncherMetaData.class));
            Mockito.verify(matcher2).match(Mockito.any(CruncherMetaData.class));
        }

        private ResultActions matchResume() throws Exception {
            return mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v2/job/match/{resumeId}", "1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("X-Auth-Token", token)
            );
        }
    }
}
