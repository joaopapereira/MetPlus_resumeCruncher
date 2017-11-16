package org.metplus.curriculum.domain


import org.metplus.curriculum.domain.cruncher.Cruncher
import org.metplus.curriculum.domain.cruncher.CrunchersList
import org.metplus.curriculum.domain.cruncher.Matcher
import org.metplus.curriculum.domain.cruncher.MatcherList

import javax.annotation.PostConstruct

/**
 * Created by Joao Pereira on 31/08/2015.
 */
abstract class CruncherInitializer(
    private val crunchersList: CrunchersList,
    private val matchersList: MatcherList) {

    /**
     * Function used to retrieve the cruncher
     * @return Cruncher to be used
     */
    abstract val cruncher: Cruncher


    /**
     * Function used to retrieve the matcher
     * @return Matcher to be used
     */
    abstract val matcher: Matcher<*,*>

    /**
     * Function used to initialize the cruncher holder bean
     */
    @PostConstruct
    fun postContructor() {
        init()
        crunchersList.addCruncher(cruncher)
        matchersList.addMatchers(matcher)
    }

    /**
     * Function called in the post constructor to initialize
     * all the needed information of the cruncher
     */
    abstract fun init()
}
