package com.micrommer.counter.model.dto

import com.micrommer.counter.model.GeoLocation
import com.micrommer.counter.model.Owner
import java.util.*

/**
 * counter (com.micrommer.counter.model.dto)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 1:11 PM, Tuesday
 */
data class CounterDto(
        val counterId : Long,
        val datetime : Date,
        val consumption : Double,
        val geoLocation : GeoLocation,
        val owners : Set<Owner>
)
