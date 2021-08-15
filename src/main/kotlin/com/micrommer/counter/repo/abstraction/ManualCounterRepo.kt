package com.micrommer.counter.repo.abstraction

import com.micrommer.counter.model.dao.RecordDao
import org.bson.types.ObjectId

/**
 * counter (com.micrommer.counter.repo.abstraction)
 * @author : imanbhlool
 * @since : Aug/14/2021 - 3:04 PM, Saturday
 */
interface ManualCounterRepo {
    fun addRecordToCounter(counterId: ObjectId, recordDao: RecordDao)
}