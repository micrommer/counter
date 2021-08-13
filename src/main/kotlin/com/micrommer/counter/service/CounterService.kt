package com.micrommer.counter.service

import com.micrommer.counter.model.dto.CounterDto
import com.micrommer.counter.repo.CounterRepo
import org.springframework.stereotype.Service

@Service
class CounterService(
    private val counterRepo: CounterRepo,
    private val notifier: EmailNotifier
) {
    enum class MessageTopic {
        INACTIVE_RECEIVED
    }

    /**
     * Add Counter Record: Adds new record to <em> active </em> counter
     */
    fun addCounterRecord(counterDto: CounterDto) {

        val entity = counterRepo.findByCounterId(counterDto.counterId)
        if (!entity.active) {
            notifier.notify(
                entity.owners.map { owner -> return@map owner.email },
                this.messageGenerator(MessageTopic.INACTIVE_RECEIVED)
            )
        }
        //TODO : create an advance mongo query service

    }


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