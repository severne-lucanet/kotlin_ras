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
    companion object {
        private const val DELIMITER = "###"
    }
    private val LOGGER = LoggerFactory.getLogger(CSVPersisterServiceImpl::class.java.name)
    private val statsFilePath = Paths.get(csvFileDirectory, compStatsFile)
    private val logFilePath = Paths.get(csvFileDirectory, logsFile)
    
    override fun saveComputerStats(computerStats:ComputerStats) {
        val sb = StringBuilder()
        sb.append(computerStats.computerUuid).append(DELIMITER)
        sb.append(computerStats.timestamp?.getEpochSecond()).append(DELIMITER)
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
        sb.append(logFile.timestamp?.getEpochSecond()).append(DELIMITER)
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
