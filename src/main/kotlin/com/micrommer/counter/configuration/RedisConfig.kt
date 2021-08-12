package com.micrommer.counter.configuration

import com.micrommer.counter.service.RedisMessageSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer


/**
 * counter (com.micrommer.counter.configuration)
 * @author : imanbhlool
 * @since : Aug/11/2021 - 1:20 PM, Wednesday
 */
@Configuration
class RedisConfig(private val redisConnectionFactory: RedisConnectionFactory,
                  private val redisMessageSubscriber: RedisMessageSubscriber) {

    /**
     * Message listener: Register our message subscriber as message listener
     *
     * @return MessageListenerAdapter
     */
    @Bean
    fun messageListener(): MessageListenerAdapter {
        return MessageListenerAdapter(redisMessageSubscriber)
    }

    /**
     * Redis container: Brings asynchronous behavior for Redis message listeners and handles the low level details of
     * listening, converting and message dispatching
     *
     * @return RedisMessageListenerContainer
     */
    @Bean
    fun redisContainer(): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        container.addMessageListener(messageListener(), topic())
        return container
    }

    /**
     * Topic: Defines a topic channel for both publishing and consuming
     *
     * @return
     */
    @Bean
    fun topic(): ChannelTopic {
        return ChannelTopic("messageQueue")
    }

    /**
     * Redis template: Registers Jackson Json Serializer for message serializing
     *
     * @param template
     * @return
     */
    @Bean
    fun redisTemplate(template: RedisTemplate<String, String>): RedisTemplate<String, String> {
        val serializer = Jackson2JsonRedisSerializer(Any::class.java)
        template.setDefaultSerializer(serializer)
        return template
    }
}