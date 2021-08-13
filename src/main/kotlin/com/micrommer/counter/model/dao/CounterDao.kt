package com.micrommer.counter.model.dao

import com.micrommer.counter.model.common.GeoLocation
import com.micrommer.counter.model.common.Owner
import org.springframework.data.annotation.Id
import java.util.*

/**
 * counter (com.micrommer.counter.model.dao)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 2:12 PM, Tuesday
 */
data class CounterDao (
        @Id val counterId: Long,
        val owners: Set<Owner>,
        val records : Set<CounterRecord>,
        var lastGeoLocation: GeoLocation? = null,
        var lastGeoUpdate: Date? = null,
        var active: Boolean = false
)