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
package com.severett.kotlin_ras.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.severett.kotlin_ras.dto.InputDTO
import com.severett.kotlin_ras.exception.StatsParserException
import com.severett.kotlin_ras.model.ComputerStats
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import javax.validation.Validation

@Service
open class StatsParserServiceImpl : StatsParserService {
    private val objMapper = ObjectMapper()
    private val validatorFactory = Validation.buildDefaultValidatorFactory()
    private val LOGGER = LoggerFactory.getLogger(StatsParserServiceImpl::class.java.name)
    
    override fun parseComputerStats(inputDTO:InputDTO<JSONObject>) : ComputerStats {
        try {
            LOGGER.debug("Parsing '${inputDTO.payload.toString()}'")
            val computerStats = objMapper.readValue(inputDTO.payload.toString(), ComputerStats::class.java)
            computerStats.computerUuid = inputDTO.computerUuid
            computerStats.timestamp = inputDTO.timestamp
            val constraintViolations = validatorFactory.validator.validate(computerStats)
            if (constraintViolations.isEmpty()) {
                return computerStats
            } else {
                throw StatsParserException(constraintViolations.map { 
                    "${it.propertyPath} value '${it.invalidValue}' ${it.message}"
                }.joinToString(";"))
            }
        } catch (ex:Exception) {
            when(ex) {
                is IOException,
                is NumberFormatException -> {
                    throw StatsParserException("Bad Format Exception: ${ex.message}")
                }
                else -> throw ex
            }
        }
    }
}
