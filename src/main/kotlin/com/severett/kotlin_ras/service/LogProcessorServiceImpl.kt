package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO
import com.severett.kotlin_ras.model.LogFile
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.bus.Event
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors

@Service
open class LogProcessorServiceImpl(
        private val persisterService:PersisterService
) : LogProcessorService {
    companion object {
        val LOGGER = LoggerFactory.getLogger(LogProcessorServiceImpl::class.java.name)
    }
    
    override fun accept(event : Event<InputDTO<ByteArray>>) {
        val inputDTO = event.getData();
        LOGGER.debug("Processing log files for computer ${inputDTO.computerUuid}")
        var inMemoryByteChannel:SeekableInMemoryByteChannel? = null
        var zipFile:ZipFile? = null
        var inputStream:InputStream? = null
        try {
            inMemoryByteChannel = SeekableInMemoryByteChannel(inputDTO.payload)
            zipFile = ZipFile(inMemoryByteChannel)
            val archiveEntry = zipFile.getEntries().nextElement()
            inputStream = zipFile.getInputStream(archiveEntry)
            val content = BufferedReader(InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"))
            val logFile = LogFile(inputDTO.computerUuid, inputDTO.timestamp, content)
            persisterService.saveLogFile(logFile)
        } catch (ioe:IOException) {
            LOGGER.error("Error processing log data from ${inputDTO.computerUuid}: ${ioe.message}")
        } finally {
            inMemoryByteChannel?.close()
            zipFile?.close()
            inputStream?.close()
        }
    }
}
