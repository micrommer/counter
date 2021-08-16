package com.micrommer.counter.service

import com.micrommer.counter.model.common.GeoLocation
import com.micrommer.counter.model.common.Owner
import com.micrommer.counter.model.dao.CounterDao
import com.micrommer.counter.model.dao.RecordDao
import com.micrommer.counter.model.dao.ZoneDao
import com.micrommer.counter.model.dto.BillingDto
import com.micrommer.counter.model.dto.CounterDto
import com.micrommer.counter.model.dto.RecordDto
import com.micrommer.counter.repo.CounterRepo
import com.micrommer.counter.repo.ZoneRepo
import com.micrommer.counter.repo.abstraction.ManualCounterRepo
import com.micrommer.counter.service.abstraction.GeoLocator
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class CounterService(
        private val counterRepo: CounterRepo,
        private val manualCounterRepo: ManualCounterRepo,
        private val notifier: EmailNotifier,
        private val geoLocator: GeoLocator,
        private val zoneRepo: ZoneRepo
) {
    @Value("\${department.top-level.email}")
    lateinit var topLevelDepartmentEmail: String

    enum class MessageTopic {
        INACTIVE_RECEIVED, DOES_NOT_EXIST_RECEIVED, ZERO_CONSUMPTION, OVER_CONSUMPTION
    }

    /**
     * Add Counter Record: Start point of a flow of some notifying and caching.
     * Adds new record to a counter, if it is not active, record will committed and owners will notified and if counter
     * doesn't exist, top level department will notified.
     * In case of receiving 0 consumption from an active Counter, we well notified owners.
     * This stage is also the best and essential stage for geo location calculation and caching.
     */
    fun addCounterRecord(record: RecordDto) {
        val entity = counterRepo.findById(record.counterId)
        if (entity.isPresent) {
            locateCounter(entity.get(), record.geoLocation)
            overUsageNotifier(record, entity.get().owners)
            if (!entity.get().active) {
                notifier.notify(
                        entity.get().owners.map { owner -> return@map owner.email },
                        this.messageGenerator(MessageTopic.INACTIVE_RECEIVED)
                )
            } else {
                if (record.consumption == 0.0) {
                    notifier.notify(
                            entity.get().owners.map { owner -> return@map owner.email },
                            this.messageGenerator(MessageTopic.ZERO_CONSUMPTION)
                    )
                }
            }
            manualCounterRepo.addRecordToCounter(record.counterId, RecordDao(record.datetime, record.consumption, record.geoLocation))
        } else {
            notifier.notify(listOf(topLevelDepartmentEmail), messageGenerator(MessageTopic.DOES_NOT_EXIST_RECEIVED))
        }
    }

    /**
     * Locate counter: Calculate and commit the id of nearest location to Counter if needed, based on criteria. It has no
     * side effect, because of its specific mongo query.
     *
     * @param counter
     * @param geo
     */
    private fun locateCounter(counter: CounterDao, geo: GeoLocation) {
        val expiration = counter.lastGeoUpdate?.before(geoLocator.lastUpdate()) ?: true
        if (counter.lastGeoLocationId == null || expiration) {
            val geoLocationId = geoLocator.getRelatedZoneId(geo.latitude, geo.longitude)
            manualCounterRepo.addGeoLocationId(counter.counterId, geoLocationId)
        }
    }

    /**
     * Over usage notifier : Check for over usage case and notify Counter's owner if required
     *
     * @param record
     * @param recipients
     */
    private fun overUsageNotifier(record: RecordDto, recipients: Set<Owner>) {
        val geoLocationId = manualCounterRepo.getLastGeoLocation(record.counterId)
        val zone = zoneRepo.findById(geoLocationId)
        val cal = Calendar.getInstance().apply {
            this.time = record.datetime
        }
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val halfOfYear = if (dayOfYear <= 180) {
            zone.get().consumption[0]
        } else {
            zone.get().consumption[1]
        }
        if (record.consumption > halfOfYear.extraUsage) {
            notifier.notify(recipients.map { owner -> return@map owner.email },
                    messageGenerator(MessageTopic.OVER_CONSUMPTION))
        }
    }


    fun addNewCounter(counterDto: CounterDto): ResponseEntity<CounterDto> {
        val entity = counterRepo.save(convertToCounterDao(counterDto))
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToCounterDto(entity))
    }

    private fun convertToCounterDao(counterDto: CounterDto): CounterDao =
            CounterDao(ObjectId(), counterDto.owners, setOf())


    private fun convertToCounterDto(counterDao: CounterDao): CounterDto =
            CounterDto(counterDao.counterId, counterDao.owners, counterDao.active)


    /**
     * Message generator: Generates messages based on topic
     *
     * @param topic
     */
    private fun messageGenerator(topic: MessageTopic) = when (topic) {
        MessageTopic.INACTIVE_RECEIVED -> {
            """
                Hello,
                There is a problem with your Counter, we've received data from your Counter while your Counter is 
                inactivated!
                
                Best Regards
            """.trimIndent()
        }
        MessageTopic.DOES_NOT_EXIST_RECEIVED -> {
            """
                Hello,
                We have received a record from unknown Counter, please handle this case.
                
                Best Regards
            """.trimIndent()
        }
        MessageTopic.ZERO_CONSUMPTION -> {
            """
                Hello,
                We have received a zero record from your Counter, please check it out or in case of non-use, explicitly
                disable it.
                
                Best Regards
            """.trimIndent()
        }
        MessageTopic.OVER_CONSUMPTION -> {
            """
                Hello,
                Your consumption was above the usage limitation, please be careful.
                
                Best Regards
            """.trimIndent()
        }
    }

    fun disableCounter(counterId: String): ResponseEntity<Unit> {
        val entity = counterRepo.findById(ObjectId(counterId))
        return if (entity.isPresent) {
            if (entity.get().active) {
                entity.get().active = false
                counterRepo.save(entity.get())
            }
            ResponseEntity.status(HttpStatus.OK).body(Unit)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(Unit)
        }
    }

    fun getBilling(counterId: String, fromDate: Date, toDate: Date): ResponseEntity<BillingDto> {
        val counterObjectId = ObjectId(counterId)
        val records = manualCounterRepo.getRecords(counterObjectId, fromDate, toDate)
        if (records.isEmpty()){
            return ResponseEntity.ok(BillingDto(0.0,0.0))
        }
        val lastGeoLocationId = manualCounterRepo.getLastGeoLocation(counterObjectId)
        val zone = zoneRepo.findById(lastGeoLocationId)
        val totalCost = records.map {
            calculateItemCost(zone.get(), it)
        }.sum()
        val totalConsumption = records.map {
            it.consumption
        }.sum()

        return ResponseEntity.ok(BillingDto(totalConsumption, totalCost))

    }

    /**
     * Calculate item cost: Calculates one record consumption rate and return its cost
     *
     * @param zone
     * @param record
     * @return
     */
    private fun calculateItemCost(zone: ZoneDao, record: RecordDao): Double {
        val cal = Calendar.getInstance().apply {
            this.time = record.datetime
        }
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val halfOfYear = if (dayOfYear <= 180) {
            zone.consumption[0]
        } else {
            zone.consumption[1]
        }
        // Calculates normal consumption with constant consumption rate
        return if (record.consumption > halfOfYear.extraUsage.toDouble()) {
            record.consumption * halfOfYear.base
        } else {
            // Calculates extra consumption with dynamic consumption rate
            val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
            val specialCost = halfOfYear.extra.map {
                // first element of pair pattern is 'num-num'
                val range = it.first.split("-")
                // second element is num
                Triple(range[0].toInt(), range[1].toInt(), it.second.toInt())
            }.filter {
                // return triple that match that match
                it.first <= hourOfDay && hourOfDay < it.second
            }

            specialCost[0].third * record.consumption
        }
    }
}