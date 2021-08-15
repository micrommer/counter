package com.micrommer.counter.model.dao

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * counter (com.micrommer.counter.model.dao)
 * @author : imanbhlool
 * @since : Aug/15/2021 - 5:17 PM, Sunday
 */
@Document
data class ZoneDao(
        @Id
        val id: ObjectId,
        @Indexed(unique = true)
        val zoneId: Int,
        val name: String,
        val lat: Double,
        val long: Double,
        val area: Double,
        val consumption: List<ConsumptionDetail>
)


data class ConsumptionDetail(
        val name: String,
        val base: Int,
        val extraUsage: Int,
        val extra: List<Pair<String, String>>
)

