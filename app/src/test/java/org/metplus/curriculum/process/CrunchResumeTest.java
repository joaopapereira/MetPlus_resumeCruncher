package org.metplus.curriculum.process;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.metplus.curriculum.cruncher.expressionCruncher.CruncherImpl;
import org.metplus.curriculum.cruncher.expressionCruncher.ExpressionCruncher;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

/**
 * Created by joaopereira on 2/7/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class CrunchResumeTest {
    @Rule
    public ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            MockitoAnnotations.initMocks(CrunchResumeTest.this);
            Mockito.when(expressionCruncher.getCruncher()).thenReturn(cruncherImpl);
        };

        @Override
        protected void after() {
        };
    };
    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private SettingsRepository repository;

    @Mock
    private ExpressionCruncher expressionCruncher;

    @Mock
    private SpringMongoConfig dbConfig;

    @Mock
    private CruncherImpl cruncherImpl;
    @InjectMocks
    CrunchResume testObject = new CrunchResume();

    @Test
    public void noResumes() {
        List<Resume> allResumes = new ArrayList<>();
        Mockito.when(resumeRepository.findAll()).thenReturn(allResumes);
        testObject.crunch();
        Mockito.verify(cruncherImpl, times(0)).calculate(anyString());
    }

    @Test
    public void oneResumeNoUpdate() {
        Resume resume = Mockito.mock(Resume.class);
        Mockito.when(resume.isCruncherDataAvailable(anyString())).thenReturn(true);
        List<Resume> allResumes = new ArrayList<>();
        allResumes.add(resume);
        Mockito.when(resumeRepository.findAll()).thenReturn(allResumes);
        testObject.crunch();
        Mockito.verify(cruncherImpl, times(0)).calculate(anyString());
    }
    @Test
    public void oneResumeNeedUpdateResumeNotFound() throws ResumeNotFound, ResumeReadException {
        Resume resume = Mockito.mock(Resume.class);
        Mockito.when(resume.isCruncherDataAvailable(anyString())).thenReturn(false);
        Mockito.when(resume.getResume(dbConfig)).thenThrow(new ResumeNotFound(""));
        List<Resume> allResumes = new ArrayList<>();
        allResumes.add(resume);
        Mockito.when(resumeRepository.findAll()).thenReturn(allResumes);
        testObject.crunch();
        Mockito.verify(cruncherImpl, times(0)).calculate(anyString());
    }

    @Test
    public void oneResumeNeedUpdateResumeReadException() throws ResumeNotFound, ResumeReadException {
        Resume resume = Mockito.mock(Resume.class);
        Mockito.when(resume.isCruncherDataAvailable(anyString())).thenReturn(false);
        Mockito.when(resume.getResume(dbConfig)).thenThrow(new ResumeReadException(""));
        List<Resume> allResumes = new ArrayList<>();
        allResumes.add(resume);
        Mockito.when(resumeRepository.findAll()).thenReturn(allResumes);
        testObject.crunch();
        Mockito.verify(cruncherImpl, times(0)).calculate(anyString());
    }

    @Test
    public void oneResumeNeedUpdateSuccess() throws ResumeNotFound, ResumeReadException {
        Resume resume = Mockito.mock(Resume.class);
        Mockito.when(resume.isCruncherDataAvailable(anyString())).thenReturn(false);
        Mockito.when(resume.getResume(dbConfig)).thenReturn(new ByteArrayOutputStream());
        List<Resume> allResumes = new ArrayList<>();
        allResumes.add(resume);
        Mockito.when(resumeRepository.findAll()).thenReturn(allResumes);
        testObject.crunch();
        Mockito.verify(cruncherImpl, times(1)).calculate(anyString());
    }
}
