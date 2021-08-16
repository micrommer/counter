package com.micrommer.counter.controller

import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.model.dto.CounterDto
import com.micrommer.counter.model.dto.RecordDto
import com.micrommer.counter.service.CounterService
import com.micrommer.counter.service.abstraction.MessagePublisher
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
class CountersController(private val counterService: CounterService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Register new Counter", notes = "Registers a new Counter and retrieves its Id for future use")
    fun create(@Valid @RequestBody counterDto: CounterDto): ResponseEntity<CounterDto> {
        return counterService.addNewCounter(counterDto)
    }
}