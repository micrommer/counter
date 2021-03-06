package com.micrommer.counter.service.abstraction

import com.micrommer.counter.model.dto.RecordDto

/**
 * counter (com.micrommer.counter.service)
 * @author : imanbhlool
 * @since : Aug/11/2021 - 1:59 PM, Wednesday
 */
interface MessagePublisher {
    fun publish(recordDto: RecordDto)
}