package com.micrommer.counter.controller

import com.micrommer.counter.model.dto.RecordDto
import com.micrommer.counter.service.CounterService
import com.micrommer.counter.service.abstraction.MessagePublisher
import org.springframework.beans.propertyeditors.CustomDateEditor
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
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

    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val custom = object : CustomDateEditor(dateFormat, true) {
            override fun setAsText(text: String?) {
                if ("today" == text) {
                    value = Date()
                } else {
                    super.setAsText(text)
                }
            }
        }
        binder.registerCustomEditor(Date::class.java, custom)
    }

    @PostMapping("/record")
    fun create(@Valid @RequestBody record: RecordDto) {
        messagePublisher.publish(record)
    }

    @PatchMapping("/{counterId}/disable")
    fun disable(@PathVariable counterId: String): ResponseEntity<*> {
        return counterService.disableCounter(counterId)
    }

    @GetMapping("/{counterId}/records/billing")
    fun getBilling(@PathVariable
                   counterId: String,
                   @RequestParam(value = "fromDate")
                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                   fromDate: Date,
                   @RequestParam(value = "toDate", required = false, defaultValue = "today")
                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                   toDate: Date = Date()): ResponseEntity<*> {
        return counterService.getBilling(counterId, fromDate, toDate)
    }


}