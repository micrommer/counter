package com.micrommer.counter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.service.abstraction.MessagePublisher
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service


/**
 * counter (com.micrommer.counter.port)
 * @author : imanbhlool
 * @since : Aug/12/2021 - 12:36 PM, Thursday
 */

@Service
class RedisMessagePublisher(
        private val redisTemplate: RedisTemplate<String, String>,
        private val topic: ChannelTopic,
        private val objectMapper: ObjectMapper
) : MessagePublisher {
    override fun publish(counterDao: CounterDao) {
        val mapped = objectMapper.writeValueAsString(counterDao)
        redisTemplate.convertAndSend(topic.topic, mapped)
    }
}