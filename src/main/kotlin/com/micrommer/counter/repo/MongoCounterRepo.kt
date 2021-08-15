package com.micrommer.counter.repo

import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.model.dao.RecordDao
import com.micrommer.counter.model.dto.RecordDto
import com.micrommer.counter.repo.abstraction.ManualCounterRepo
import org.bson.types.ObjectId
import org.springframework.data.mongodb.MongoCollectionUtils
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.util.*

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

    override fun addGeoLocationId(counterId: ObjectId, geoLocationId: ObjectId) {
        mongo.updateFirst(
                Query.query(Criteria.where(CounterDao::counterId.name).`is`(counterId)),
                Update().set(CounterDao::lastGeoLocationId.name, geoLocationId)
                        .set(CounterDao::lastGeoUpdate.name, Date()),
                CounterDao::class.java
        )
    }

    override fun getRecords(counterId: ObjectId, fromDate: Date, toDate: Date): List<RecordDao> {
        val idCriteria = Criteria.where("_id").`is`(counterId)
        val unwindStage = Aggregation.unwind("\$records")
        val projectionStage = Aggregation.project().andInclude("records").andExclude("_id")
        val matchCriteria = Criteria().andOperator(
                Criteria.where("records.datetime").gte(fromDate),
                Criteria.where("records.datetime").lte(toDate)
        )
        val replacementStage = Aggregation.replaceRoot("\$records")
        val aggregationStages = listOf(
                Aggregation.match(idCriteria),
                unwindStage,
                projectionStage,
                Aggregation.match(matchCriteria),
                replacementStage
        )

        val aggregation = Aggregation.newAggregation(aggregationStages)

        println(aggregation.toString())

        val res = mongo.aggregate(
                aggregation,
                MongoCollectionUtils.getPreferredCollectionName(CounterDao::class.java),
                RecordDao::class.java
        )
        return res.mappedResults
    }

}