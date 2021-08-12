package com.micrommer.counter.model.dao

import com.micrommer.counter.model.common.GeoLocation
import com.micrommer.counter.model.common.Owner
import java.util.*

/**
 * counter (com.micrommer.counter.model.dao)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 2:12 PM, Tuesday
 */
data class CounterDto (
        val counterId: Long,
        val datetime: Date,
        val consumption: Double,
        val geoLocation: GeoLocation,
        val owners: Set<Owner>,
        var lastGeoLocation: GeoLocation? = null,
        var lastGeoUpdate: Date? = null,
        var active: Boolean = false
)