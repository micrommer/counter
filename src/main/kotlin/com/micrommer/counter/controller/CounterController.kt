package com.micrommer.counter.controller

import com.micrommer.counter.model.dto.BillingDto
import com.micrommer.counter.model.dto.RecordDto
import com.micrommer.counter.service.CounterService
import com.micrommer.counter.service.abstraction.MessagePublisher
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.propertyeditors.CustomDateEditor
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
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
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add a new record", notes = "Submit a new record and the record will manipulate asynchronous, so the response is not accurate")
    fun create(@Valid @RequestBody record: RecordDto) {
        messagePublisher.publish(record)
    }

    @PatchMapping("/{counterId}/disable")
    @ApiOperation(value = "Disable a Counter", notes = "Disable a counter and in case of it is already disabled, does noting")
    @ApiResponses(
            ApiResponse(code = 404, message = "Not Found")
    )
    fun disable(@PathVariable counterId: String): ResponseEntity<Unit> {
        return counterService.disableCounter(counterId)
    }

    @GetMapping("/{counterId}/records/billing")
    @ApiOperation(value = "Expose a billing", notes = "Calculate and expose a billing based on the dates")
    fun getBilling(@PathVariable
                   counterId: String,
                   @RequestParam(value = "fromDate")
                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                   fromDate: Date,
                   @RequestParam(value = "toDate", required = false, defaultValue = "today")
                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                   toDate: Date = Date()): ResponseEntity<BillingDto> {
        return counterService.getBilling(counterId, fromDate, toDate)
    }


}