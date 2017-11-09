package org.metplus.curriculum.process;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.metplus.curriculum.database.domain.JobMongo;
import org.metplus.curriculum.database.repository.JobDocumentRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joao on 3/28/16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({JobCruncherTests.Run.class})
public class JobCruncherTests {
    @RunWith(MockitoJUnitRunner.class)
    public static class Run {
        @Mock
        private CrunchersList allCrunchers;
        @Mock
        private JobDocumentRepository jobRepository;
        @InjectMocks
        private JobCruncher jobCruncher;

        @Mock
        private Cruncher cruncherImpl;
        @Test
        public void noJobs() throws InterruptedException {
            jobCruncher.postConstructor();
            jobCruncher.stop();
            jobCruncher.join();
            Mockito.verify(allCrunchers, Mockito.times(0)).getCrunchers();
            Mockito.verify(jobRepository, Mockito.times(0)).save(Mockito.<JobMongo>any());
        }
        @Test
        public void oneJob() throws InterruptedException {
            JobMongo job = Mockito.mock(JobMongo.class);
            List<Cruncher> listCrunchers = new ArrayList<>();
            listCrunchers.add(cruncherImpl);
            Mockito.when(allCrunchers.getCrunchers()).thenReturn(listCrunchers);
            jobCruncher.postConstructor();
            jobCruncher.addWork(job);
            jobCruncher.stop();
            jobCruncher.join();
            Mockito.verify(allCrunchers, Mockito.times(1)).getCrunchers();
            Mockito.verify(jobRepository, Mockito.times(1)).save(job);
        }
        @Test
        public void twoJobs() throws InterruptedException {
            JobMongo job1 = new JobMongo();
            job1.setTitle("title 1");
            job1.setDescription("description 1");
            JobMongo job2 = new JobMongo();
            job2.setTitle("title 2");
            job2.setDescription("description 2");
            List<Cruncher> listCrunchers = new ArrayList<>();
            listCrunchers.add(cruncherImpl);
            Mockito.when(allCrunchers.getCrunchers()).thenReturn(listCrunchers);
            jobCruncher.postConstructor();
            jobCruncher.addWork(job1);
            jobCruncher.addWork(job2);

            jobCruncher.stop();
            jobCruncher.join();
            Mockito.verify(allCrunchers, Mockito.times(2)).getCrunchers();
            Mockito.verify(cruncherImpl).crunch("title 1");
            Mockito.verify(cruncherImpl).crunch("description 1");
            Mockito.verify(cruncherImpl).crunch("title 2");
            Mockito.verify(cruncherImpl).crunch("description 2");
            Mockito.verify(jobRepository).save(job1);
            Mockito.verify(jobRepository).save(job2);
        }
    }
}
