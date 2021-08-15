package com.micrommer.counter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.model.dao.RecordDao
import com.micrommer.counter.model.dto.CounterDto
import com.micrommer.counter.model.dto.RecordDto
import com.micrommer.counter.repo.CounterRepo
import com.micrommer.counter.repo.abstraction.ManualCounterRepo
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CounterService(
        private val counterRepo: CounterRepo,
        private val manualCounterRepo: ManualCounterRepo,
        private val notifier: EmailNotifier,
        private val om: ObjectMapper
) {
    enum class MessageTopic {
        INACTIVE_RECEIVED
    }

    /**
     * Add Counter Record: Adds new record to <em> active </em> counter
     */
    fun addCounterRecord(record: RecordDto) {

        val entity = counterRepo.findById(record.counterId)
        if (entity.isPresent) {
            val presentEntity = entity.get()
            if (!presentEntity.active) {
                notifier.notify(
                        presentEntity.owners.map { owner -> return@map owner.email },
                        this.messageGenerator(MessageTopic.INACTIVE_RECEIVED)
                )
            }
            manualCounterRepo.addRecordToCounter(record.counterId, RecordDao(record.datetime, record.consumption, record.geoLocation))
        }


    }

    fun addNewCounter(counterDto: CounterDto): ResponseEntity<*> {
        val entity = counterRepo.save(convertToCounterDao(counterDto))
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToCounterDto(entity))
    }

    private fun convertToCounterDao(counterDto: CounterDto): CounterDao =
            CounterDao(ObjectId(), counterDto.owners, setOf())


    private fun convertToCounterDto(counterDao: CounterDao): CounterDto =
            CounterDto(counterDao.counterId, counterDao.owners, counterDao.active)


    private fun messageGenerator(topic: MessageTopic) = when (topic) {
        MessageTopic.INACTIVE_RECEIVED -> {
            """
                Hi,
                There is a problem with your Counter, we've received data from your Counter while your Counter is 
                inactivated!
                
                Thanks
            """.trimIndent()
        }

    }


}