package com.severett.kotlin_ras.controller

import com.severett.kotlin_ras.dto.InputDTO
import com.severett.kotlin_ras.reactor.SAEventBus
import com.severett.kotlin_ras.service.LogProcessorService
import com.severett.kotlin_ras.service.StatProcessorService
import org.apache.commons.compress.utils.IOUtils
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.bus.Event
import reactor.bus.selector.ObjectSelector
import java.io.IOException
import java.io.InputStream
import java.time.Instant
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/stats")
open class StatsAggregatorController(
        private val eventBus:SAEventBus,
        statProcessorService:StatProcessorService,
        logProcessorService:LogProcessorService
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(StatsAggregatorController::class.java.name)
        val STATS_EVENT = "statistics"
        val LOGS_EVENT = "log_files"
    }
    init {
        this.eventBus.on(ObjectSelector<String, String>(STATS_EVENT), statProcessorService)
        this.eventBus.on(ObjectSelector<String, String>(LOGS_EVENT), logProcessorService)
    }
    
    @RequestMapping(value = "/{computerUuid}/upload_statistics", method = arrayOf(RequestMethod.POST), consumes = arrayOf("application/json"))
    fun uploadStats(response:HttpServletResponse, @PathVariable("computerUuid") computerUuid:String,
                    @RequestParam("timestamp") timestamp:Long?, @RequestBody requestBody:String) {
        LOGGER.debug("Received statistics upload from $computerUuid: $requestBody")
        if (timestamp != null) {
            try {
                eventBus.notify(STATS_EVENT, Event.wrap(InputDTO<JSONObject>(computerUuid, JSONObject(requestBody), Instant.ofEpochSecond(timestamp))))
                response.setStatus(HttpServletResponse.SC_OK)
            } catch (jsone:JSONException) {
                LOGGER.error("Error parsing JSON stats data from $computerUuid: ${jsone.message}")
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            } catch (e:Exception) {
                LOGGER.error("Unexpected error in uploadStats: ${e.message} (${e.javaClass.name})")
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            }
        } else {
            LOGGER.warn("Record did not have a timestamp attached - skipping")
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        }
    }
    
    @RequestMapping(value = "/{computerUuid}/upload_logs", method = arrayOf(RequestMethod.POST), consumes = arrayOf("application/zip"))
    fun uploadLogs(response:HttpServletResponse, @PathVariable("computerUuid") computerUuid:String,
                   @RequestParam("timestamp") timestamp:Long?, zipInputStream:InputStream) {
        LOGGER.debug("Received log file upload from {}", computerUuid);
        if (timestamp != null) {
            try {
                // Need to transfer the input stream in the controller, otherwise
                // the input stream will close when this function terminates
                eventBus.notify(LOGS_EVENT, Event.wrap(InputDTO<ByteArray>(computerUuid, IOUtils.toByteArray(zipInputStream), Instant.ofEpochSecond(timestamp))))
                response.setStatus(HttpServletResponse.SC_OK)
            } catch (ioe:IOException) {
                LOGGER.error("Error parsing log data from $computerUuid: ${ioe.message}")
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            } catch (e:Exception) {
                LOGGER.error("Unexpected error in uploadStats: ${e.message} (${e.javaClass.name})")
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            }
        } else {
            LOGGER.warn("Log file did not have a timestamp attached - skipping")
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        }
    }
}
