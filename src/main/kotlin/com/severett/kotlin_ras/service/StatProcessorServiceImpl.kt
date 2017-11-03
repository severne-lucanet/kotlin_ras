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

import com.severett.kotlin_ras.dto.InputDTO
import com.severett.kotlin_ras.exception.StatsParserException
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
open class StatProcessorServiceImpl(
        private val statsParserService:StatsParserService,
        private val persisterService:PersisterService
) : StatProcessorService {
    private val LOGGER = LoggerFactory.getLogger(StatProcessorServiceImpl::class.java.name)

    override fun onSuccess(inputDTO:InputDTO<JSONObject>) {
        val computerUuid = inputDTO.computerUuid
        LOGGER.debug("Processing statistics for computer $computerUuid")
        try {
            val computerStats = statsParserService.parseComputerStats(inputDTO)
            persisterService.saveComputerStats(computerStats)
        } catch (spe:StatsParserException) {
            LOGGER.error("Statistics parsing error for computer $computerUuid: ${spe.message}")
        }
    }

    override fun onError(e:Throwable) {
        LOGGER.error("Error in StatProcessorServiceImpl: ${e.message}")
    }

    override fun onSubscribe(disposable:Disposable) {
        //No-op
    }
}
