package com.micrommer.counter.repo

import com.micrommer.counter.model.dao.CounterDao
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface CounterRepo : MongoRepository<CounterDao, ObjectId> {
}