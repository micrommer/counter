package com.micrommer.counter.model.common

import javax.validation.constraints.Max
import javax.validation.constraints.Min

/**
 * counter (com.micrommer.counter.model)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 1:57 PM, Tuesday
 */
data class GeoLocation(
        @field:Min(-90)
        @field:Max(90)
        val latitude: Double,
        @field:Min(-180)
        @field:Max(180)
        val longitude: Double
)
