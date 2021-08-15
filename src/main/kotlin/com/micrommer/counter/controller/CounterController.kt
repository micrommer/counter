package com.micrommer.counter.controller

import com.micrommer.counter.model.dto.RecordDto
import com.micrommer.counter.service.CounterService
import com.micrommer.counter.service.abstraction.MessagePublisher
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * counter (com.micrommer.counter.controller)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 3:38 PM, Tuesday
 */
@Controller
@RequestMapping("/counter")
class CounterController(private val messagePublisher: MessagePublisher,
                        private val counterService: CounterService) {

    @PostMapping("/record")
    fun create(@Valid @RequestBody record: RecordDto) {
        messagePublisher.publish(record)
    }

    @PatchMapping("/{counterId}/disable")
    fun disable(@PathVariable counterId: String): ResponseEntity<*> {
        return counterService.disableCounter(counterId)
    }


}