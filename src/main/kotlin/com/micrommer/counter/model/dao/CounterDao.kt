package com.micrommer.counter.model.dao

import com.micrommer.counter.model.common.GeoLocation
import com.micrommer.counter.model.common.Owner
import com.micrommer.counter.model.dto.CounterDto
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.util.*

/**
 * counter (com.micrommer.counter.model.dao)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 2:12 PM, Tuesday
 */
data class CounterDao(
        @Id
        val counterId: ObjectId,
        val owners: Set<Owner>,
        val records: Set<RecordDao>,
        var lastGeoLocation: GeoLocation? = null,
        var lastGeoUpdate: Date? = null,
        var active: Boolean = true
)
