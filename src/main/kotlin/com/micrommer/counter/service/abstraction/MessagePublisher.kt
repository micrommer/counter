package com.micrommer.counter.service.abstraction

import com.micrommer.counter.model.dao.CounterDto

/**
 * counter (com.micrommer.counter.service)
 * @author : imanbhlool
 * @since : Aug/11/2021 - 1:59 PM, Wednesday
 */
interface MessagePublisher {
    fun publish(counterDto: CounterDto)
}