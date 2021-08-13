package com.micrommer.counter.repo

import com.micrommer.counter.model.dao.CounterDao
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface CounterRepo : MongoRepository<CounterDao, ObjectId> {
    fun findByCounterId(counterId : Long) : CounterDao
}