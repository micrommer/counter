package com.micrommer.counter.repo

import com.micrommer.counter.model.dao.ZoneDao
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * counter (com.micrommer.counter.repo)
 * @author : imanbhlool
 * @since : Aug/15/2021 - 5:59 PM, Sunday
 */
interface ZoneRepo : MongoRepository<ZoneDao, ObjectId> {
    fun findByZoneId(zoneId: Int): ZoneDao
}