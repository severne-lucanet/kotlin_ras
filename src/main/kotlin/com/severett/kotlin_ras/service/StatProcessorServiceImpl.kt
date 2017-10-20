package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO
import com.severett.kotlin_ras.exception.StatsParserException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.bus.Event

@Service
open class StatProcessorServiceImpl(
        private val statsParserService:StatsParserService,
        private val persisterService:PersisterService
) : StatProcessorService {
    companion object {
        val LOGGER = LoggerFactory.getLogger(StatProcessorServiceImpl::class.java.name)
    }
    
    override fun accept(event : Event<InputDTO<JSONObject>>) {
        val inputDTO = event.data
        val computerUuid = inputDTO.computerUuid
        LOGGER.debug("Processing statistics for computer $computerUuid")
        try {
            val computerStats = statsParserService.parseComputerStats(inputDTO)
            persisterService.saveComputerStats(computerStats)
        } catch (spe:StatsParserException) {
            LOGGER.error("Statistics parsing error for computer $computerUuid: ${spe.message}")
        }
    }
}
