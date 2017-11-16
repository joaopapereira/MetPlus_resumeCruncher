package org.metplus.curriculum.cruncher.expressionCruncher

import org.metplus.curriculum.domain.cruncher.Matcher
import org.metplus.curriculum.database.domain.*
import org.metplus.curriculum.database.repository.JobDocumentRepository
import org.metplus.curriculum.database.repository.ResumeRepository
import org.slf4j.LoggerFactory
import java.util.Collections

import kotlin.collections.Map

/**
 * Created by Joao on 3/21/16.
 * Class that implement the Matcher for Resumes using the Expression Cruncher
 */
class MatcherImpl
/**
 * Class constructor
 * @param cruncher Cruncher implementation
 * @param resumeRepository Resume repository to retrieve the resumes
 */
(private val cruncher: CruncherImpl,
 private val resumeRepository: ResumeRepository,
 private val jobRepository: JobDocumentRepository
) : Matcher<Resume, JobMongo> {
    override val cruncherName: String
        get() = "ExpressionMatcher"

    fun match(title: String, description: String): List<Resume>? {
        logger.trace("match($title, $description)")
        val titleExpression: String
        val descriptionExpression: String

        try {
            // Crunch the title
            titleExpression = (cruncher.crunch(title) as ExpressionCruncherMetaData).mostReferedExpression
            // Crunch the description
            descriptionExpression = (cruncher.crunch(description) as ExpressionCruncherMetaData).mostReferedExpression
        } catch (exp: Exception) {
            logger.error("Unable to retrieve the most common expression")
            logger.error(exp.localizedMessage)
            return null
        }

        return matchResumes(titleExpression, descriptionExpression)
    }

    override fun matchInverse(job: JobMongo): List<Resume>? {
        logger.trace("match($job)")
        val titleExpression: String
        val descriptionExpression: String
        try {
            titleExpression = (job.getTitleCruncherData(cruncherName) as ExpressionCruncherMetaData).mostReferedExpression
            descriptionExpression = (job.getDescriptionCruncherData(cruncherName) as ExpressionCruncherMetaData).mostReferedExpression
        } catch (exp: Exception) {
            logger.error("Unable to retrieve the most common expression")
            logger.error(exp.localizedMessage)
            return null
        }

        return matchResumes(titleExpression, descriptionExpression)
    }

    private fun matchResumes(titleExpression: String, descriptionExpression: String): List<Resume> {
        // Retrieve all the resumes
        val resumes = resumeRepository.resumesOnCriteria(ResumeComparator())
        val resultTitle = ArrayList<Resume>()
        val resultDescription = ArrayList<Resume>()
        for (resume in resumes) {
            logger.debug("Checking viability of the resume: " + resume)
            // Retrieve the meta data of the resume
            val metaDataCruncher = resume.getCruncherData(cruncher.cruncherName) as ExpressionCruncherMetaData? ?: continue
            var resumeExpression: String? = metaDataCruncher.mostReferedExpression
            if (resumeExpression == null || resumeExpression.length == 0)
                resumeExpression = resume.getCruncherData(cruncher.cruncherName)!!
                    .getOrderedFields(ResumeFieldComparator())[0].key
            // Does resume and title have the same most common expression
            if (resumeExpression.compareTo(titleExpression) == 0) {
                logger.debug("Resume checks up with the title")
                resultTitle.add(resume)
                // Does resume and description have the same most common expression
            } else if (resumeExpression.compareTo(descriptionExpression) == 0) {
                logger.debug("Resume checks up with the description")
                resultDescription.add(resume)
            }
        }
        // Sort the resumes that match title to have on top the one with more expressions
        Collections.sort(resultTitle, ResumeSorter())
        // Sort the resumes that match description to have on top the one with more expressions
        Collections.sort(resultDescription, ResumeSorter())
        resultTitle.addAll(resultDescription)
        return resultTitle
    }

    override fun matchSimilarity(resume: Resume, job: JobMongo): Double {
        logger.trace("match($job)")
        val titleExpression: String
        val descriptionExpression: String
        try {
            titleExpression = (job.getTitleCruncherData(cruncherName) as ExpressionCruncherMetaData).mostReferedExpression
            descriptionExpression = (job.getDescriptionCruncherData(cruncherName) as ExpressionCruncherMetaData).mostReferedExpression
        } catch (exp: Exception) {
            logger.error("Unable to retrieve the most common expression")
            logger.error(exp.localizedMessage)
            return 0.0
        }

        val resultTitle = ArrayList<Resume>()
        val resultDescription = ArrayList<Resume>()
        logger.debug("Checking viability of the resume: " + resume)
        // Retrieve the meta data of the resume
        val metaDataCruncher = resume.getCruncherData(cruncher.cruncherName) as ExpressionCruncherMetaData?
        if (metaDataCruncher != null) {
            var resumeExpression: String? = metaDataCruncher.mostReferedExpression
            if (resumeExpression == null || resumeExpression.length == 0)
                resumeExpression = resume.getCruncherData(cruncher.cruncherName)!!
                    .getOrderedFields(ResumeFieldComparator())[0].key
            // Does resume and title have the same most common expression
            if (resumeExpression.compareTo(titleExpression) == 0) {
                logger.debug("Resume checks up with the title")
                resultTitle.add(resume)
                // Does resume and description have the same most common expression
            } else if (resumeExpression.compareTo(descriptionExpression) == 0) {
                logger.debug("Resume checks up with the description")
                resultDescription.add(resume)
            }
        }
        return (resultTitle.size * 3 + resultDescription.size * 2).toDouble()
    }

    override fun match(resume: Resume): List<JobMongo>? {
        val metadata = resume.getCruncherData(cruncher.cruncherName)
        logger.trace("match($metadata)")
        // Retrieve the meta data into a good object type
        val auxMetaData = metadata as ExpressionCruncherMetaData?
        val result = ArrayList<JobMongo>()
        if (auxMetaData == null) {
            logger.error("Invalid metadata was passed to the function: ")
            return null
        }
        // Iterate over all the jobs
        for (job in jobRepository.findAll()) {
            logger.debug("Checking viability of the job: " + job.jobId)
            val jobMetaData = job.getTitleCruncherData(cruncherName) as ExpressionCruncherMetaData?
            // Check if the most common denominator between the job and the meta data is the same
            if (jobMetaData == null || jobMetaData.mostReferedExpression == null) {
                logger.error("JobMongo with id: " + job.jobId + " do not have all the information")
                continue
            }
            if (jobMetaData.mostReferedExpression == auxMetaData.mostReferedExpression) {
                logger.debug("JobMongo match with metadata")
                result.add(job)
            }
        }
        // Sort the jobs to add to the top the most relevant
        Collections.sort(result, JobSorter())
        return result
    }

    override fun getCruncherName(): String {
        return cruncher.cruncherName
    }

    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private class ResumeFieldComparator : MetaDataComparator {
        override fun compare(o1: Map.Entry<String, MetaDataField<*>>, o2: Map.Entry<String, MetaDataField<*>>): Int {
            val left = o1.value.data as Int
            val right = o2.value.data as Int
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

    /**
     * Class that will compare the resumes
     * to group them by most common expression
     */
    private inner class ResumeComparator : Comparator<Resume> {

        override fun compare(o1: Resume, o2: Resume): Int {
            val leftFields = o1.getCruncherData(cruncher.cruncherName)!!
                .getOrderedFields(ResumeFieldComparator())
            val rightFields = o2.getCruncherData(cruncher.cruncherName)!!
                .getOrderedFields(ResumeFieldComparator())
            if (leftFields.size == 0)
                return -1
            return if (rightFields.size == 0) 1 else leftFields[0].key.compareTo(rightFields[0].key)
        }
    }

    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private abstract inner class EntitySorter<T : DocumentWithMetaData> : Comparator<T> {
        override fun compare(o1: T, o2: T): Int {
            val field = getFieldName(o1)
            val left = getFieldValue(o1, field)
            val right = getFieldValue(o2, field)
            if (left < right)
                return 1
            else if (left > right)
                return -1
            return 0
        }

        protected abstract fun getFieldName(obj: T): String
        protected abstract fun getFieldValue(obj: T, fieldName: String): Int
    }

    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private inner class ResumeSorter : EntitySorter<Resume>() {

        override fun getFieldName(obj: Resume): String {
            return (obj.getCruncherData(cruncher.cruncherName) as ExpressionCruncherMetaData)
                .mostReferedExpression
        }

        override fun getFieldValue(obj: Resume, fieldName: String): Int {
            return (obj.getCruncherData(cruncher.cruncherName)!!
                .fields[fieldName]?.data as Int?)!!
        }
    }

    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private inner class JobSorter : EntitySorter<JobMongo>() {

        override fun getFieldName(obj: JobMongo): String {
            return (obj.getTitleCruncherData(cruncher.cruncherName) as ExpressionCruncherMetaData)
                .mostReferedExpression
        }

        override fun getFieldValue(obj: JobMongo, fieldName: String): Int {
            return (obj.getTitleCruncherData(cruncher.cruncherName)!!
                .fields[fieldName]?.data as Int?)!!
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MatcherImpl::class.java)
    }
}
