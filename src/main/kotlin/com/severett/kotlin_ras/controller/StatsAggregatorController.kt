/**
 * Copyright 2017 Severn Everett
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.severett.kotlin_ras.controller

import com.severett.kotlin_ras.dto.InputDTO
import com.severett.kotlin_ras.service.LogProcessorService
import com.severett.kotlin_ras.service.StatProcessorService
import io.reactivex.rxkotlin.toSingle
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
import java.io.IOException
import java.io.InputStream
import java.time.Instant
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/stats")
open class StatsAggregatorController(
        private val statProcessorService:StatProcessorService,
        private val logProcessorService:LogProcessorService
) {
    private val LOGGER = LoggerFactory.getLogger(StatsAggregatorController::class.java.name)
    
    @RequestMapping(value = "/{computerUuid}/upload_statistics", method = arrayOf(RequestMethod.POST), consumes = arrayOf("application/json"))
    fun uploadStats(response:HttpServletResponse, @PathVariable("computerUuid") computerUuid:String,
                    @RequestParam("timestamp") timestamp:Long?, @RequestBody requestBody:String) {
        LOGGER.debug("Received statistics upload from $computerUuid: $requestBody")
        if (timestamp != null) {
            try {
                InputDTO<JSONObject>(computerUuid, JSONObject(requestBody), Instant.ofEpochSecond(timestamp))
                        .toSingle()
                        .subscribe(statProcessorService)
                response.status = HttpServletResponse.SC_OK
            } catch (jsone:JSONException) {
                LOGGER.error("Error parsing JSON stats data from $computerUuid: ${jsone.message}")
                response.status = HttpServletResponse.SC_BAD_REQUEST
            } catch (e:Exception) {
                LOGGER.error("Unexpected error in uploadStats: ${e.message} (${e.javaClass.name})")
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            }
        } else {
            LOGGER.warn("Record did not have a timestamp attached - skipping")
            response.status = HttpServletResponse.SC_BAD_REQUEST
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
                InputDTO<ByteArray>(computerUuid, IOUtils.toByteArray(zipInputStream), Instant.ofEpochSecond(timestamp))
                        .toSingle()
                        .subscribe(logProcessorService)
                response.status = HttpServletResponse.SC_OK
            } catch (ioe:IOException) {
                LOGGER.error("Error parsing log data from $computerUuid: ${ioe.message}")
                response.status = HttpServletResponse.SC_BAD_REQUEST
            } catch (e:Exception) {
                LOGGER.error("Unexpected error in uploadStats: ${e.message} (${e.javaClass.name})")
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            }
        } else {
            LOGGER.warn("Log file did not have a timestamp attached - skipping")
            response.status = HttpServletResponse.SC_BAD_REQUEST
        }
    }
}
