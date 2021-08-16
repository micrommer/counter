package com.micrommer.counter.model.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.bson.types.ObjectId

/**
 * @author : iman
 * @since : 7/13/2020, Mon
 *
 * Costume Jackson serializer for converting ObjectId and list of ObjectId into String representation
 */
class ObjectIdToString : JsonSerializer<Any>() {
    override fun serialize(value: Any?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        when (value) {
            is ObjectId -> {
                gen!!.writeObject(value.toString())
            }
            is Collection<*> -> {
                val list = mutableListOf<String>()
                value.stream().forEach {
                    list.add((it as ObjectId).toString())
                }
                gen!!.writeArray(list.toTypedArray(),0,list.size)
            }
            else ->{
                throw RuntimeException("try to assign non-predefine type to ObjectIdToString")
            }
        }
    }

}