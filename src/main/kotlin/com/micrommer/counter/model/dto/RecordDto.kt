package com.micrommer.counter.model.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.micrommer.counter.model.common.GeoLocation
import com.micrommer.counter.model.dao.RecordDao
import com.micrommer.counter.model.util.ObjectIdToString
import org.bson.types.ObjectId
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Min

/**
 * counter (com.micrommer.counter.model.dto)
 * @author : imanbhlool
 * @since : Aug/14/2021 - 3:31 PM, Saturday
 */
data class RecordDto(
        @field:JsonSerialize(using = ObjectIdToString::class)
        val counterId: ObjectId,
        val datetime: Date,
        @field:Min(0)
        val consumption: Double,
        @field:Valid val
        geoLocation: GeoLocation,
)