package com.micrommer.counter.controller

import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.service.abstraction.MessagePublisher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.Valid

/**
 * counter (com.micrommer.counter.controller)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 3:38 PM, Tuesday
 */
@Controller
@RequestMapping("/counters")
class CountersController(private val messagePublisher: MessagePublisher) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody counter: CounterDao) {
        messagePublisher.publish(counter)
    }
}