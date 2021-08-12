package com.micrommer.counter.model.dto

import com.micrommer.counter.model.common.GeoLocation
import com.micrommer.counter.model.common.Owner
import com.micrommer.counter.model.dao.CounterDto
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Min

/**
 * counter (com.micrommer.counter.model.dto)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 1:11 PM, Tuesday
 */
data class CounterDto(
        @field:Min(1) val counterId: Long,
        val datetime: Date,
        @field:Min(0) val consumption: Double,
        @field:Valid val geoLocation: GeoLocation,
        @field:Valid val owners: Set<Owner>,
        var active: Boolean = false
) : DaoCompatible<CounterDto> {
    override fun getDao(): CounterDto {
        return CounterDto(
                counterId,
                datetime,
                consumption,
                geoLocation,
                owners,
                active = active
        )
    }


}
