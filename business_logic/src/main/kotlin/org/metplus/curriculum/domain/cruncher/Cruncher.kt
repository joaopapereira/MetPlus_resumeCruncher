package org.metplus.curriculum.domain.cruncher

/**
 * Created by Joao Pereira on 31/08/2015.
 */
interface Cruncher {

    /**
     * Retrieve the name of the cruncher
     * @return Name of the cruncher
     */
    val cruncherName: String

    /**
     * Function that will do the work of generating
     * the meta data associated with a input
     * @param data Input to be processed
     * @return Processed data
     */
    fun crunch(data: String): CruncherMetaData

}
