package org.metplus.curriculum.domain.cruncher

/**
 * Created by joao on 3/22/16.
 * Component that will store all the matchers
 */
class MatcherList {

    internal var matchers: MutableList<Matcher<*, *>> = mutableListOf()

    /**
     * All the matchers
     * @return List of all the matchers
     */
    fun getMatchers(): MutableList<Matcher<*, *>> {
        return matchers
    }

    /**
     * Add a new matcher
     * @param matcher Matcher
     */
    fun addMatchers(matcher: Matcher<*, *>) {
        getMatchers().add(matcher)
    }
}
