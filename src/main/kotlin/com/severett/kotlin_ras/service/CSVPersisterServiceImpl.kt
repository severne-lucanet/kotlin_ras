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

import com.severett.kotlin_ras.model.ComputerStats
import com.severett.kotlin_ras.model.LogFile
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@Service
open class CSVPersisterServiceImpl (
        @Value("\${com.severett.rapid_stats_aggregator.csv.directory}") csvFileDirectory:String,
        @Value("\${com.severett.rapid_stats_aggregator.csv.compStatsFile}") compStatsFile:String,
        @Value("\${com.severett.rapid_stats_aggregator.csv.logsFile}") logsFile:String
) : PersisterService {
    private val LOGGER = LoggerFactory.getLogger(CSVPersisterServiceImpl::class.java.name)
    private val statsFilePath = Paths.get(csvFileDirectory, compStatsFile)
    private val logFilePath = Paths.get(csvFileDirectory, logsFile)
    init {
        Files.createDirectories(Paths.get(csvFileDirectory))
    }
    companion object {
        private const val DELIMITER = "###"
    }
    
    override fun saveComputerStats(computerStats:ComputerStats) {
        val sb = StringBuilder()
        sb.append(computerStats.computerUuid).append(DELIMITER)
        sb.append(computerStats.timestamp?.epochSecond).append(DELIMITER)
        sb.append(computerStats.productVersion).append(DELIMITER)
        sb.append(computerStats.operatingSystem).append(DELIMITER)
        sb.append(computerStats.processCPULoad).append(DELIMITER)
        sb.append(computerStats.systemCPULoad).append(DELIMITER)
        sb.append(computerStats.memoryUsage).append(DELIMITER)
        sb.append(computerStats.memoryCapacity).append("\n")
        
        try {
            LOGGER.debug("Persisting computer stats to {}", statsFilePath);
            synchronized (statsFilePath) {
                Files.write(statsFilePath, sb.toString().toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
            }
        } catch (ioe:IOException) {
            LOGGER.error("Error writting to $statsFilePath: ${ioe.message}");
        }
    }
    
    override fun saveLogFile(logFile:LogFile) {
        val sb = StringBuilder();
        sb.append(logFile.computerUuid).append(DELIMITER)
        sb.append(logFile.timestamp?.epochSecond).append(DELIMITER)
        sb.append(logFile.content?.replace("\n", "\t")).append("\n")
        
        try {
            LOGGER.debug("Persisting log entry to {}", logFilePath);
            synchronized (logFilePath) {
                Files.write(logFilePath, sb.toString().toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (ioe:IOException) {
            LOGGER.error("Error writting to $statsFilePath: ${ioe.message}");
        }
    }
}
