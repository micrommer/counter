package com.micrommer.counter.model.dao

import com.micrommer.counter.model.common.GeoLocation
import java.util.*

data class RecordDao(
    val datetime: Date,
    val consumption: Double,
    val geoLocation: GeoLocation
)