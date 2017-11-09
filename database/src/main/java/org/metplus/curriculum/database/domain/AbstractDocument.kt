package org.metplus.curriculum.database.domain


import java.math.BigInteger

import com.mongodb.DBObject
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener

abstract class AbstractDocument(
    @Id var id: BigInteger? = null
)