package org.metplus.curriculum.domain.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.metplus.curriculum.domain.buildJob
import org.metplus.curriculum.domain.cruncher.Cruncher
import org.metplus.curriculum.domain.cruncher.CruncherMetaData
import org.metplus.curriculum.domain.cruncher.CrunchersList
import org.metplus.curriculum.domain.job.testDoubles.JobRepositoryFake

class CruncherStub(override val cruncherName: String) : Cruncher {
    var stubbedCrunch: MutableMap<String, CruncherMetaData> = mutableMapOf()
    var throwCrunch: MutableMap<String, Exception> = mutableMapOf()

    override fun crunch(data: String): CruncherMetaData {
        if (throwCrunch[data] == null) {
            return stubbedCrunch[data]!!
        }
        throw throwCrunch[data]!!
    }
}

class CrunchJobTest {
    private lateinit var crunchJob: CrunchJob
    private lateinit var jobRepositoryFake: JobRepositoryFake
    private lateinit var cruncherStub: CruncherStub
    private lateinit var cruncherList: CrunchersList

    @Before
    fun `set up`() {
        cruncherStub = CruncherStub("theCruncher")
        cruncherList = CrunchersList()
        cruncherList.addCruncher(cruncherStub)
        jobRepositoryFake = JobRepositoryFake()
        crunchJob = CrunchJob(
            jobRepository = jobRepositoryFake,
            crunchersList = cruncherList
        )
    }

    @Test
    fun `when process successfully, it saves the crunch results`() {
        val job = jobRepositoryFake.save(buildJob(jobId = "1"))

        cruncherStub.stubbedCrunch.put(job.description, JobCruncherDataMap(
            mapOf(
                "description key" to JobCruncherDataField<Long>(1)
            )
        ))
        cruncherStub.stubbedCrunch.put(job.title, JobCruncherDataMap(
            mapOf(
                "title key" to JobCruncherDataField<Long>(1)
            )
        ))
        crunchJob.crunch(job)

        val savedJob = jobRepositoryFake.findById("1")
        assertThat(savedJob).isEqualToIgnoringGivenFields(
            job,
            "descriptionMetaData",
            "titleMetaData"
        )
        assertThat(savedJob!!.descriptionMetaData!!.cruncherData).isEqualTo(
            mapOf(
                "theCruncher" to JobCruncherDataMap(
                    mapOf(
                        "description key" to JobCruncherDataField<Long>(1)
                    )
                )
            )
        )
        assertThat(savedJob.titleMetaData!!.cruncherData).isEqualTo(
            mapOf(
                "theCruncher" to JobCruncherDataMap(
                    mapOf(
                        "title key" to JobCruncherDataField<Long>(1)
                    )
                )
            )
        )
    }

    @Test
    fun `when crunching title data throws exception, it continues and titleData is empty`() {
        val job = jobRepositoryFake.save(buildJob(jobId = "1"))

        cruncherStub.stubbedCrunch.put(job.description, JobCruncherDataMap(
            mapOf(
                "description key" to JobCruncherDataField<Long>(1)
            )
        ))
        cruncherStub.throwCrunch.put(job.title, Exception())
        crunchJob.crunch(job)

        val savedJob = jobRepositoryFake.findById("1")
        assertThat(savedJob).isEqualToIgnoringGivenFields(
            job,
            "descriptionMetaData",
            "titleMetaData"
        )
        assertThat(savedJob!!.descriptionMetaData!!.cruncherData).isEqualTo(
            mapOf(
                "theCruncher" to JobCruncherDataMap(
                    mapOf(
                        "description key" to JobCruncherDataField<Long>(1)
                    )
                )
            )
        )
        assertThat(savedJob.titleMetaData!!.cruncherData).isEqualTo(
            mapOf(
                "theCruncher" to JobCruncherDataMap(
                    emptyMap()
                )
            )
        )
    }

    @Test
    fun `when crunching description data throws exception, it continues and descriptionData is empty`() {
        val job = jobRepositoryFake.save(buildJob(jobId = "1"))

        cruncherStub.stubbedCrunch.put(job.title, JobCruncherDataMap(
            mapOf(
                "title key" to JobCruncherDataField<Long>(1)
            )
        ))
        cruncherStub.throwCrunch.put(job.description, Exception())
        crunchJob.crunch(job)

        val savedJob = jobRepositoryFake.findById("1")
        assertThat(savedJob).isEqualToIgnoringGivenFields(
            job,
            "descriptionMetaData",
            "titleMetaData"
        )
        assertThat(savedJob!!.titleMetaData!!.cruncherData).isEqualTo(
            mapOf(
                "theCruncher" to JobCruncherDataMap(
                    mapOf(
                        "title key" to JobCruncherDataField<Long>(1)
                    )
                )
            )
        )
        assertThat(savedJob.descriptionMetaData!!.cruncherData).isEqualTo(
            mapOf(
                "theCruncher" to JobCruncherDataMap(
                    emptyMap()
                )
            )
        )
    }
}