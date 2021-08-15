package com.micrommer.counter.repo

import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.model.dao.RecordDao
import com.micrommer.counter.repo.abstraction.ManualCounterRepo
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

/**
 * counter (com.micrommer.counter.repo)
 * @author : imanbhlool
 * @since : Aug/14/2021 - 3:07 PM, Saturday
 */

@Service
class MongoCounterRepo(private val mongo: MongoTemplate) : ManualCounterRepo {

    override fun addRecordToCounter(counterId: ObjectId, recordDao: RecordDao) {
        mongo.updateFirst(
                Query.query(Criteria.where(CounterDao::counterId.name).`is`(counterId)),
                Update().addToSet(CounterDao::records.name, recordDao),
                CounterDao::class.java
        )
    }
}