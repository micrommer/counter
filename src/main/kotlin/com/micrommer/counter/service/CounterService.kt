package com.micrommer.counter.service

import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.model.dao.RecordDao
import com.micrommer.counter.model.dto.CounterDto
import com.micrommer.counter.model.dto.RecordDto
import com.micrommer.counter.repo.CounterRepo
import com.micrommer.counter.repo.abstraction.ManualCounterRepo
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CounterService(
        private val counterRepo: CounterRepo,
        private val manualCounterRepo: ManualCounterRepo,
        private val notifier: EmailNotifier
) {
    @Value("\${department.top-level.email}")
    lateinit var topLevelDepartmentEmail: String

    enum class MessageTopic {
        INACTIVE_RECEIVED, DOES_NOT_EXIST_RECEIVED, ZERO_CONSUMPTION
    }

    /**
     * Add Counter Record: Adds new record to a counter, if it is not active, record will committed and owners will
     * notified and if counter doesn't exist, top level department will notified.
     * In case of receiving 0 consumption from an active Counter, we well notified owners.
     */
    fun addCounterRecord(record: RecordDto) {

        val entity = counterRepo.findById(record.counterId)
        if (entity.isPresent) {
            if (!entity.get().active) {
                notifier.notify(
                        entity.get().owners.map { owner -> return@map owner.email },
                        this.messageGenerator(MessageTopic.INACTIVE_RECEIVED)
                )
            } else {
                if (record.consumption == 0.0) {
                    notifier.notify(
                            entity.get().owners.map { owner -> return@map owner.email },
                            this.messageGenerator(MessageTopic.ZERO_CONSUMPTION)
                    )
                }
            }
            manualCounterRepo.addRecordToCounter(record.counterId, RecordDao(record.datetime, record.consumption, record.geoLocation))
        } else {
            notifier.notify(listOf(topLevelDepartmentEmail), messageGenerator(MessageTopic.DOES_NOT_EXIST_RECEIVED))
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
                Hello,
                There is a problem with your Counter, we've received data from your Counter while your Counter is 
                inactivated!
                
                Best Regards
            """.trimIndent()
        }
        MessageTopic.DOES_NOT_EXIST_RECEIVED -> {
            """
                Hello,
                We have received a record from unknown Counter, please handle this case.
                
                Best Regards
            """.trimIndent()
        }
        MessageTopic.ZERO_CONSUMPTION -> {
            """
                Hello,
                We have received a zero record from your Counter, please check it out or in case of non-use, explicitly
                disable it.
                
                Best Regards
            """.trimIndent()
        }
    }

    fun disableCounter(counterId: String): ResponseEntity<Unit> {
        val entity = counterRepo.findById(ObjectId(counterId))
        return if (entity.isPresent) {
            if (entity.get().active) {
                entity.get().active = false
                counterRepo.save(entity.get())
            }
            ResponseEntity.status(HttpStatus.OK).body(Unit)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(Unit)
        }
    }
}