package com.micrommer.counter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.model.dto.CounterDto
import com.micrommer.counter.model.dto.RecordDto
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service


/**
 * counter (com.micrommer.counter.service)
 * @author : imanbhlool
 * @since : Aug/12/2021 - 1:02 PM, Thursday
 */
@Service
class RedisMessageSubscriber(private val objectMapper: ObjectMapper,
                             private val counterService: CounterService) : MessageListener {
    private val logger = LoggerFactory.getLogger(javaClass)

    var messageList = mutableListOf<String>()

    override fun onMessage(p0: Message, p1: ByteArray?) {
        val obj = objectMapper.readValue(p0.body, RecordDto::class.java)
        messageList.add(p0.toString())
        logger.info("Message received: $obj")
        logger.info(messageList.size.toString())

        counterService.addCounterRecord(obj)
    }
}