package com.micrommer.counter.controller

import com.micrommer.counter.model.dto.CounterDto
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
class CountersController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody counter: CounterDto) {
        println(counter.counterId)
    }
}