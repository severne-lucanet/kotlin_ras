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
    companion object {
        val LOGGER = LoggerFactory.getLogger(StatsParserServiceImpl::class.java.name)
    }
    
    override fun parseComputerStats(inputDTO:InputDTO<JSONObject>) : ComputerStats {
        try {
            LOGGER.debug("Parsing '${inputDTO.payload.toString()}'")
            val computerStats = objMapper.readValue(inputDTO.payload.toString(), ComputerStats::class.java)
            computerStats.computerUuid = inputDTO.computerUuid
            computerStats.timestamp = inputDTO.timestamp
            val validator = validatorFactory.getValidator()
            val constraintViolations = validator.validate(computerStats)
            if (constraintViolations.isEmpty()) {
                return computerStats
            } else {
                throw StatsParserException(constraintViolations.map { 
                    "${it.getPropertyPath()} value '${it.getInvalidValue()}' ${it.getMessage()}"
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
