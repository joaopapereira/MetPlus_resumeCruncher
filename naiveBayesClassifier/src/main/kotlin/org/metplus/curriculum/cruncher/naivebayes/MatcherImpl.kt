package org.metplus.curriculum.cruncher.naivebayes

import org.metplus.curriculum.cruncher.Matcher
import org.metplus.curriculum.database.domain.*
import org.metplus.curriculum.database.repository.JobDocumentRepository
import org.metplus.curriculum.database.repository.ResumeRepository
import java.util.*
import java.util.Arrays.stream
import kotlin.streams.toList

class MatcherImpl internal constructor(
    private val cruncher: CruncherImpl,
    private val jobRepository: JobDocumentRepository,
    private val resumeRepository: ResumeRepository
) : Matcher<Resume, JobMongo> {
    private val matchPoints = arrayOf(arrayOf(1600.0, 1200.0, 1000.0, 900.0, 850.0), arrayOf(750.0, 800.0, 600.0, 500.0, 450.0), arrayOf(700.0, 350.0, 400.0, 300.0, 250.0), arrayOf(600.0, 300.0, 150.0, 200.0, 150.0), arrayOf(400.0, 200.0, 100.0, 50.0, 100.0))

    override fun match(resume: Resume?): List<JobMongo> {
        val result = ArrayList<JobMongo>()
        if (!isResumeValid(resume))
            return result

        val resumeCategories = getCategoryListFromMetaData(resume, MAX_NUMBER_CATEGORIES)

        val allJobs = jobRepository.findAll() as List<JobMongo>
        for (job in allJobs) {
            val jobCategories = getJobCategories(job)

            val starRating = calculateStarRating(resumeCategories, jobCategories)

            if (starRating > 0) {
                job.starRating = starRating
                result.add(job)
            }
        }
        return result
    }

    override fun matchInverse(job: JobMongo?): List<Resume> {
        val results = ArrayList<Resume>()
        if (!isJobValid(job))
            return results
        val allResumes = resumeRepository.findAll() as List<Resume>

        val jobCategories = getJobCategories(job!!)

        for (resume in allResumes) {
            val resumeCategories = getCategoryListFromMetaData(resume, MAX_NUMBER_CATEGORIES)

            val starRating = calculateStarRating(jobCategories, resumeCategories)
            if (starRating > 0) {
                resume.starRating = starRating
                results.add(resume)
            }
        }

        return results
    }

    override fun getCruncherName(): String {
        return cruncher.cruncherName
    }

    override fun matchSimilarity(resume: Resume?, job: JobMongo?): Double {
        if (!isResumeValid(resume) || !isJobValid(job))
            return 0.0

        val jobCategories = getJobCategories(job!!)
        val resumeCategories = getCategoryListFromMetaData(resume, MAX_NUMBER_CATEGORIES)

        return calculateStarRating(resumeCategories, jobCategories)
    }

    private fun getJobCategories(job: JobMongo): List<String> {
        val jobCategories = getCategoryListFromMetaData(job.titleMetaData, 2)
        jobCategories.addAll(getCategoryListFromMetaData(job.descriptionMetaData,
            MAX_NUMBER_CATEGORIES - jobCategories.size))
        return jobCategories
    }

    private fun getCategoryListFromMetaData(resume: DocumentWithMetaData?, limit: Int): MutableList<String> {
        return stream<Any>(resume!!
            .getCruncherData(cruncher.cruncherName)!!
            .getOrderedFields(DoubleFieldComparator()).toTypedArray())
            .map<String> { category ->
                val categoryName = (category as Map.Entry<String, Double>).key
                categoryName.replace("_job|_resume".toRegex(), "")
            }
            .distinct()
            .limit(limit.toLong())
            .toList()
            .toMutableList()
    }

    private fun isResumeValid(resume: Resume?): Boolean {
        return !(resume == null ||
            resume.metaData.isEmpty() ||
            resume.metaData[cruncher.cruncherName]!!
                .fields.isEmpty())
    }

    private fun isJobValid(job: JobMongo?): Boolean {
        return !(job == null || !job.haveCruncherData(cruncher.cruncherName))
    }

    protected fun calculateStarRating(base: List<String>, compare: List<String>): Double {
        var probability = 0.0
        var i = 0
        for (strToFind in compare) {
            if (base.contains(strToFind)) {
                probability += matchPoints[base.indexOf(strToFind)][i]
            }
            i++
        }
        return probability / maxPoints * 5
    }

    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private class DoubleFieldComparator : MetaDataComparator {

        override fun compare(o1: Map.Entry<String, MetaDataField<*>>, o2: Map.Entry<String, MetaDataField<*>>): Int {
            val left = o1.value.data as Double
            val right = o2.value.data as Double
            if (left < right)
                return 1
            else if (left > right)
                return -1
            return 0
        }

        override fun equals(other: Any?): Boolean {
            return other!!.javaClass.name == Resume::class.java.name
        }
    }

    companion object {
        private val maxPoints = 1600.0 + 800.0 + 400.0 + 200.0 + 100.0
        private val MAX_NUMBER_CATEGORIES = 5
    }
}

