package com.severett.kotlin_ras.service

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import com.severett.kotlin_ras.dto.InputDTO
import com.severett.kotlin_ras.util.TestConstants
import org.apache.commons.compress.utils.IOUtils
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.Clock

@RunWith(MockitoJUnitRunner::class)
class LogProcessorServiceTest {
    
    lateinit private var logProcessorService:LogProcessorService
    
    @Mock lateinit private var persister:PersisterService
    
    @Mock lateinit private var appender:Appender<ILoggingEvent>
    
    @Captor lateinit private var captorLoggingEvent:ArgumentCaptor<LoggingEvent>
    
    lateinit private var logger:Logger
    
    @Before
    fun setup() {
        logProcessorService = LogProcessorServiceImpl(persister)
        logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        logger.addAppender(appender)
    }
    
    @After
    fun teardown() {
        logger.detachAppender(appender)
    }
    
    @Test
    fun createLogFileTest() {
        val testLogFile = File(TestConstants.GOOD_RESOURCES_DIRECTORY.getAbsoluteFile(), "lorem_ipsum.zip")
        var inputStream:InputStream? = null
        try {
            inputStream = FileInputStream(testLogFile)
            logProcessorService.onSuccess(InputDTO<ByteArray>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()))
            Mockito.verify(appender, Mockito.times(1)).doAppend(captorLoggingEvent.capture())
        } finally {
            inputStream?.close()
        }
    }
    
    @Test
    fun noLogFileTest() {
        val testLogFile = File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "nothing.zip")
        var inputStream:InputStream? = null
        try {
            inputStream = FileInputStream(testLogFile)
            logProcessorService.onSuccess(InputDTO<ByteArray>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()))
            Mockito.verify(appender, Mockito.times(2)).doAppend(captorLoggingEvent.capture())
            Assert.assertThat(captorLoggingEvent.getAllValues().stream().anyMatch({
                    it.formattedMessage.equals("Error processing log data from abc123: archive is not a ZIP archive")
                }), Is.`is`(true))
        } finally {
            inputStream?.close()
        }
    }
    
    @Test
    fun uncompressedLogFileTest() {
        val testLogFile = File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "uncompressed.txt")
        var inputStream:InputStream? = null
        try {
            inputStream = FileInputStream(testLogFile)
            logProcessorService.onSuccess(InputDTO<ByteArray>("abc123", IOUtils.toByteArray(inputStream), Clock.systemUTC().instant()))
            Mockito.verify(appender, Mockito.times(2)).doAppend(captorLoggingEvent.capture())
            Assert.assertThat(captorLoggingEvent.getAllValues().stream().anyMatch( {
                    it.getFormattedMessage().equals("Error processing log data from abc123: archive is not a ZIP archive")
                }), Is.`is`(true))
        } finally {
            inputStream?.close()
        }
    }
}
