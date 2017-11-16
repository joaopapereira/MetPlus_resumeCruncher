package org.metplus.curriculum.domain.cruncher

/**
 * Created by joao on 3/21/16.
 * Interface for the matchers of resumes with title and description of jobs
 * @param <Entry> Resume type
 * @param <Result> Job Type
</Result></Entry> */
interface Matcher<Entry, Result> {

    /**
     * Retrieve the name of the cruncher associated with the
     * matcher
     * @return Name of the cruncher
     */
    val cruncherName: String

    fun match(entry: Entry): List<Result>
    fun matchInverse(entry: Result): List<Entry>

    /**
     * Check the similarity between a resume and a job
     * @param resume
     * @param job
     * @return Range between 0 and 5 being 5 similar
     */
    fun matchSimilarity(resume: Entry, job: Result): Double
}
