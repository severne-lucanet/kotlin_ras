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
import com.severett.kotlin_ras.model.LogFile
import io.reactivex.disposables.Disposable
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors

@Service
open class LogProcessorServiceImpl(
        private val persisterService:PersisterService
) : LogProcessorService {
    private val LOGGER = LoggerFactory.getLogger(LogProcessorServiceImpl::class.java.name)

    override fun onSuccess(inputDTO:InputDTO<ByteArray>) {
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

    override fun onError(e:Throwable) {
        LOGGER.error("Error in LogProcessorServiceImpl: ${e.message}")
    }

    override fun onSubscribe(disposable:Disposable) {
        //No-op
    }
}
