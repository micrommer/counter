package com.micrommer.counter.service.abstraction

import org.bson.types.ObjectId
import java.util.*

/**
 * counter (com.micrommer.counter.service.abstraction)
 * @author : imanbhlool
 * @since : Aug/15/2021 - 5:14 PM, Sunday
 */
interface GeoLocator {
    fun lastUpdate(): Date
    fun getRelatedZoneId(lat: Double, long: Double): ObjectId
}