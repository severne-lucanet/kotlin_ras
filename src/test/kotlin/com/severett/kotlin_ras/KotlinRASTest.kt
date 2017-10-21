package com.severett.kotlin_ras

import com.severett.kotlin_ras.util.TestConstants
import org.apache.commons.compress.utils.IOUtils
import org.hamcrest.core.Is
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Clock

@RunWith(SpringRunner::class)
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = arrayOf(MainApp::class))
@AutoConfigureMockMvc
class KotlinRASTest {
    
    @Autowired lateinit private var mvc:MockMvc
    
    @Value("\${com.severett.rapid_stats_aggregator.csv.directory}")
    lateinit private var csvDirectoryName:String
    
    @Value("\${com.severett.rapid_stats_aggregator.csv.compStatsFile}")
    lateinit private var csvStatsFileName:String
    
    @Value("\${com.severett.rapid_stats_aggregator.csv.logsFile}")
    lateinit private var csvLogsFileName:String
    
    lateinit private var csvStatsFile:File
    
    lateinit private var csvLogsFile:File
    
    @Before
    fun setup() {
        csvStatsFile = Paths.get(csvDirectoryName, csvStatsFileName).toFile()
        csvLogsFile = Paths.get(csvDirectoryName, csvLogsFileName).toFile()
        csvStatsFile.delete()
        csvLogsFile.delete()
    }
    
    @Test
    fun sendStatsTest() {
        val abc123Content = JSONObject()
        abc123Content.put("operatingSystem", "Windows 10")
        abc123Content.put("productVersion", "3.6.9")
        abc123Content.put("processCPULoad", 50.5)
        abc123Content.put("systemCPULoad", 85.5)
        abc123Content.put("memoryCapacity", 1000000)
        abc123Content.put("memoryUsage", 100000)
        
        mvc.perform(MockMvcRequestBuilders.post("/stats/abc123/upload_statistics")
                .param("timestamp", Clock.systemUTC().instant().getEpochSecond().toString())
                .contentType("application/json")
                .content(abc123Content.toString())
            ).andExpect(MockMvcResultMatchers.status().isOk())
        // Need to wait for the stats to be parsed and persisted, as this
        // is an asynchronous operation
        Thread.sleep(1000)
        val xyz890Content = JSONObject()
        xyz890Content.put("operatingSystem", "OSX")
        xyz890Content.put("productVersion", "2.4.6")
        xyz890Content.put("processCPULoad", 35.5)
        xyz890Content.put("systemCPULoad", 60.5)
        xyz890Content.put("memoryCapacity", 1000000)
        xyz890Content.put("memoryUsage", 100000)
        
        mvc.perform(MockMvcRequestBuilders.post("/stats/xyz890/upload_statistics")
                .param("timestamp", Clock.systemUTC().instant().getEpochSecond().toString())
                .contentType("application/json")
                .content(xyz890Content.toString())
            ).andExpect(MockMvcResultMatchers.status().isOk());
        // Need to wait for the stats to be parsed and persisted, as this
        // is an asynchronous operation
        Thread.sleep(1000)
        val csvStatsLines = String(Files.readAllBytes(csvStatsFile.toPath())).split("\n").filter { it.isNotEmpty() }
        Assert.assertThat(csvStatsLines.size, Is.`is`(2))
        val abc123StatsStr = csvStatsLines[0].split("###")
        Assert.assertThat(abc123StatsStr.size, Is.`is`(8))
        Assert.assertThat(abc123StatsStr[0], Is.`is`("abc123"))
        Assert.assertThat(abc123StatsStr[2], Is.`is`("3.6.9"))
        Assert.assertThat(abc123StatsStr[3], Is.`is`("Windows 10"))
        Assert.assertThat(abc123StatsStr[4], Is.`is`("50.5"))
        Assert.assertThat(abc123StatsStr[5], Is.`is`("85.5"))
        Assert.assertThat(abc123StatsStr[6], Is.`is`("100000"))
        Assert.assertThat(abc123StatsStr[7], Is.`is`("1000000"))
        
        val xyz890StatsStr = csvStatsLines[1].split("###")
        Assert.assertThat(xyz890StatsStr.size, Is.`is`(8))
        Assert.assertThat(xyz890StatsStr[0], Is.`is`("xyz890"))
        Assert.assertThat(xyz890StatsStr[2], Is.`is`("2.4.6"))
        Assert.assertThat(xyz890StatsStr[3], Is.`is`("OSX"))
        Assert.assertThat(xyz890StatsStr[4], Is.`is`("35.5"))
        Assert.assertThat(xyz890StatsStr[5], Is.`is`("60.5"))
        Assert.assertThat(xyz890StatsStr[6], Is.`is`("100000"))
        Assert.assertThat(xyz890StatsStr[7], Is.`is`("1000000"))
    }
    
    @Test
    fun setLogFileTest() {
        val testLogFile = File(TestConstants.GOOD_RESOURCES_DIRECTORY.getAbsoluteFile(), "app_log.zip")
        var fileInputStream:InputStream? = null
        try {
            fileInputStream = FileInputStream(testLogFile)
            mvc.perform(MockMvcRequestBuilders.post("/stats/abc123/upload_logs")
                .param("timestamp", Clock.systemUTC().instant().getEpochSecond().toString())
                .contentType("application/zip")
                .content(IOUtils.toByteArray(fileInputStream))
            ).andExpect(MockMvcResultMatchers.status().isOk())
            Thread.sleep(1000)
            val csvStatsLines = String(Files.readAllBytes(csvLogsFile.toPath())).split("\n").filter { it.isNotEmpty() }
            Assert.assertThat(csvStatsLines.size, Is.`is`(1))
        } finally {
            fileInputStream?.close()
        }
    }
    
    @Test
    fun excludeTimestampTest() {
        val abc123Content = JSONObject()
        abc123Content.put("operatingSystem", "Windows 10")
        abc123Content.put("productVersion", "3.6.9")
        abc123Content.put("processCPULoad", 50.5)
        abc123Content.put("systemCPULoad", 85.5)
        abc123Content.put("memoryCapacity", 1000000)
        abc123Content.put("memoryUsage", 100000)
        
        mvc.perform(MockMvcRequestBuilders.post("/stats/abc123/upload_statistics")
                .contentType("application/json")
                .content(abc123Content.toString())
            ).andExpect(MockMvcResultMatchers.status().isBadRequest())
    }
    
    @Test
    fun nullTimestampTest() {
        val abc123Content = JSONObject()
        abc123Content.put("operatingSystem", "Windows 10")
        abc123Content.put("productVersion", "3.6.9")
        abc123Content.put("processCPULoad", 50.5)
        abc123Content.put("systemCPULoad", 85.5)
        abc123Content.put("memoryCapacity", 1000000)
        abc123Content.put("memoryUsage", 100000)
        
        mvc.perform(MockMvcRequestBuilders.post("/stats/abc123/upload_statistics")
                .param("timestamp", "")
                .contentType("application/json")
                .content(abc123Content.toString())
            ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
    @Test
    fun sendBadStatsTest() {
        val abc123Content = JSONObject()
        val opSystem:String? = null
        val prodVersion:String? = null
        abc123Content.put("operatingSystem", opSystem)
        abc123Content.put("productVersion", prodVersion)
        abc123Content.put("processCPULoad", "Fifty")
        abc123Content.put("systemCPULoad", "Eighty")
        abc123Content.put("memoryCapacity", "One Million")
        abc123Content.put("memoryUsage", "One Hundred Thousand")
        
        mvc.perform(MockMvcRequestBuilders.post("/stats/abc123/upload_statistics")
                .param("timestamp", Clock.systemUTC().instant().getEpochSecond().toString())
                .contentType("application/json")
                .content(abc123Content.toString())
            ).andExpect(MockMvcResultMatchers.status().isOk())
        
        Thread.sleep(1000)
        Assert.assertThat(csvStatsFile.getTotalSpace(), Is.`is`(0L))
    }
    
    @Test
    fun setNoStatsTest() {
        mvc.perform(MockMvcRequestBuilders.post("/stats/abc123/upload_statistics")
                .param("timestamp", Clock.systemUTC().instant().getEpochSecond().toString())
                .contentType("application/json")
                .content("TEST FAILURE")
            ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
    @Test
    fun noLogTest() {
        val testLogFile = File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "nothing.zip")
        var fileInputStream:InputStream? = null
        try {
            fileInputStream = FileInputStream(testLogFile)
            mvc.perform(MockMvcRequestBuilders.post("/stats/abc123/upload_logs")
                .param("timestamp", Clock.systemUTC().instant().getEpochSecond().toString())
                .contentType("application/zip")
                .content(IOUtils.toByteArray(fileInputStream))
            ).andExpect(MockMvcResultMatchers.status().isOk())
            Thread.sleep(1000)
            Assert.assertThat(csvLogsFile.getTotalSpace(), Is.`is`(0L))
        } finally {
            fileInputStream?.close()
        }
    }
    
    @Test
    fun uncompressedLogTest() {
        val testLogFile = File(TestConstants.BAD_RESOURCES_DIRECTORY.getAbsoluteFile(), "uncompressed.txt")
        var fileInputStream:InputStream? = null
        try {
            fileInputStream = FileInputStream(testLogFile)
            mvc.perform(MockMvcRequestBuilders.post("/stats/abc123/upload_logs")
                .param("timestamp", Clock.systemUTC().instant().getEpochSecond().toString())
                .contentType("application/zip")
                .content(IOUtils.toByteArray(fileInputStream))
            ).andExpect(MockMvcResultMatchers.status().isOk())
            Thread.sleep(1000)
            Assert.assertThat(csvLogsFile.getTotalSpace(), Is.`is`(0L))
        } finally {
            fileInputStream?.close()
        }
    }
}
