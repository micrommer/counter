package com.micrommer.counter.model.dto

import com.micrommer.counter.model.util.ObjectIdToString
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.micrommer.counter.model.common.Owner
import org.bson.types.ObjectId
import javax.validation.Valid

/**
 * counter (com.micrommer.counter.model.dto)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 1:11 PM, Tuesday
 */
data class CounterDto(
        @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @field:JsonSerialize(using = ObjectIdToString::class)
        var counterId: ObjectId? = null,
        @field:Valid
        val owners: Set<Owner>,
        @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
        val active: Boolean? = null
)