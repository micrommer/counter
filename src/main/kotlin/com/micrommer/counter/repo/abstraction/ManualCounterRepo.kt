package com.micrommer.counter.repo.abstraction

import com.micrommer.counter.model.dao.RecordDao
import org.bson.types.ObjectId
import java.util.*

/**
 * counter (com.micrommer.counter.repo.abstraction)
 * @author : imanbhlool
 * @since : Aug/14/2021 - 3:04 PM, Saturday
 */
interface ManualCounterRepo {
    fun addRecordToCounter(counterId: ObjectId, recordDao: RecordDao)
    fun addGeoLocationId(counterId: ObjectId, geoLocationId: ObjectId)
    fun getRecords(counterId: ObjectId,fromDate : Date, toDate : Date) : List<RecordDao>
}