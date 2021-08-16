package com.micrommer.counter.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.micrommer.counter.model.dao.ConsumptionDetail
import com.micrommer.counter.model.dao.ZoneDao
import com.micrommer.counter.repo.ZoneRepo
import com.micrommer.counter.service.abstraction.GeoLocator
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.lang.Math.toRadians
import java.lang.ref.SoftReference
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct
import kotlin.math.*

/**
 * counter (com.micrommer.counter.service)
 * @author : imanbhlool
 * @since : Aug/15/2021 - 5:39 PM, Sunday
 */
// Concrete Implementation
@Service
class GeoLocatorService(@Qualifier("webApplicationContext")
                        private val resourceLoader: ResourceLoader,
                        private val zoneRepo: ZoneRepo,
                        private val objectMapper: ObjectMapper) : GeoLocator {

    @Value("\${geo-locator-update-date}")
    lateinit var lastUpdate: String

    @Value("\${geo-locator-file-name}")
    lateinit var fileName: String

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun initialize() {
        val count = zoneRepo.count()
        if (count == 0L) {
            logger.info("Loading zone data into database ...")
            val json = loadJson() as ObjectNode
            traverse(json, ::persistInDb)
        }
    }

    /**
     * Load json : Load Json file and maps it to a tree
     *
     * @return
     */
    private fun loadJson(): JsonNode {
        val res = resourceLoader.getResource(fileName)
        return objectMapper.readTree(res.inputStream)
    }

    /**
     * Persist in db : Commit Json's elements to Database
     *
     * @param node
     */
    private fun persistInDb(node: ObjectNode) {
        val dao = serializer(node)
        zoneRepo.insert(dao)
    }

    /**
     * Serializer : Serializes each element to Kotlin Data Class, Be careful this method has a strong dependency with
     * Json file structure
     *
     * @param node
     * @return
     */
    private fun serializer(node: ObjectNode): ZoneDao {
        return ZoneDao(
                ObjectId(),
                node["id"].asInt(),
                node["name"].asText(),
                node["lat"].asDouble(),
                node["long"].asDouble(),
                node["area"].asDouble(),
                (node["consumption"] as ArrayNode).map { cons ->
                    (ConsumptionDetail(
                            cons.fields().next().key,
                            cons[cons.fields().next().key]["base"].asInt(),
                            cons[cons.fields().next().key]["extraUsage"].asInt(),

                            cons[cons.fields().next().key]["extra"].map { ex ->
                                Pair(ex.fields().next().key, ex[ex.fields().next().key].asText())
                            }
                    ))
                }

        )
    }

    private fun traverse(node: ObjectNode, func: (ObjectNode) -> Unit) {
        if (!node.has("child")) {
            return
        }
        val child: ArrayNode = node.get("child") as ArrayNode

        child.forEach {
            traverse(it as ObjectNode, func)
        }

        func(node)
    }

    /**
     * Dist from: Calculates distance between two point
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    fun distFrom(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371000.0 //meters
        val dLat = toRadians((lat2 - lat1))
        val dLng = toRadians((lng2 - lng1))
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c)
    }

    /**
     * Last update : Returns last Json update date, for keeps data sync
     *
     * @return
     */
    override fun lastUpdate(): Date {
        return SimpleDateFormat("yyyy-MM-dd").parse(lastUpdate)
    }

    /**
     * Get related zone id: Traverses tree to find best match, it resolves hierarchy-ish structure of calculation
     *
     * @param lat
     * @param long
     * @return
     */
    override fun getRelatedZoneId(lat: Double, long: Double): ObjectId {
        val json = SoftReference(loadJson())
        var zoneLevel: JsonNode? = json.get()
        while (true) {
            var flag = true
            if ((zoneLevel?.get("child") as ArrayNode).isEmpty) {
                break
            }
            for (item in (zoneLevel?.get("child") as ArrayNode)) {
                val area = item?.get("area")?.asDouble() ?: 0.0
                val zoneLat = item?.get("lat")?.asDouble() ?: 0.0
                val zoneLong = item?.get("long")?.asDouble() ?: 0.0
                if (distFrom(lat, long, zoneLat, zoneLong) <= area) {
                    zoneLevel = item
                    flag = false
                    break
                }
            }
            if (flag) {
                break
            }

        }
        val zoneId = zoneLevel?.get("id")?.asInt() ?: 0
        val entity = zoneRepo.findByZoneId(zoneId)
        return entity.id
    }
}