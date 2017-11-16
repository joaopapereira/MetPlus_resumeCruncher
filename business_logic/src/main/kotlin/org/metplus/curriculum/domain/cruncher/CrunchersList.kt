package org.metplus.curriculum.domain.cruncher

/**
 * Created by joao on 3/16/16.
 */
class CrunchersList {

    private var allCrunchers: MutableList<Cruncher> = mutableListOf()

    /**
     * Retrieve all crunchers
     * @return List with all the crunchers registered
     */
    val crunchers: MutableList<Cruncher>
        get() {
            return allCrunchers
        }

    /**
     * Add a new cruncher to the list of crunchers
     * @param cruncher Cruncher to be added
     */
    fun addCruncher(cruncher: Cruncher) {
        crunchers.add(cruncher)
    }
}
