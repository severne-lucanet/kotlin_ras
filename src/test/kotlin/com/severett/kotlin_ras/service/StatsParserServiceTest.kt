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
import org.hamcrest.core.Is
import org.hamcrest.core.IsNull
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal
import java.time.Clock

class StatsParserServiceTest() {
    
    private val statsParserService:StatsParserService = StatsParserServiceImpl()
    
    @Test
    fun parseStats() {
        val inputJSON = JSONObject()
        inputJSON.put("operatingSystem", "Windows 10")
        inputJSON.put("productVersion", "3.6.9")
        inputJSON.put("processCPULoad", 50.5)
        inputJSON.put("systemCPULoad", 85.5)
        inputJSON.put("memoryCapacity", 1000000)
        inputJSON.put("memoryUsage", 100000)
        try {
            val parsedStats = statsParserService.parseComputerStats(InputDTO<JSONObject>("abc123", inputJSON, Clock.systemUTC().instant()))
            Assert.assertThat(parsedStats.operatingSystem, Is.`is`("Windows 10"))
            Assert.assertThat(parsedStats.productVersion, Is.`is`("3.6.9"))
            Assert.assertThat(parsedStats.processCPULoad, Is.`is`(BigDecimal(50.5)))
            Assert.assertThat(parsedStats.systemCPULoad, Is.`is`(BigDecimal(85.5)))
            Assert.assertThat(parsedStats.memoryCapacity, Is.`is`(1000000L))
            Assert.assertThat(parsedStats.memoryUsage, Is.`is`(100000L))
        } catch (e:Exception) {
            Assert.fail(e.message);
        }
    }
    
    @Test
    fun parseIncompleteStats() {
        val inputJSON = JSONObject()
        inputJSON.put("operatingSystem", "Windows 10");
        inputJSON.put("productVersion", "3.6.9");
        inputJSON.put("processCPULoad", 50.5);
        inputJSON.put("systemCPULoad", 85.5);
        try {
            val parsedStats = statsParserService.parseComputerStats(InputDTO<JSONObject>("abc123", inputJSON, Clock.systemUTC().instant()));
            Assert.assertThat(parsedStats.operatingSystem, Is.`is`("Windows 10"))
            Assert.assertThat(parsedStats.productVersion, Is.`is`("3.6.9"))
            Assert.assertThat(parsedStats.processCPULoad, Is.`is`(BigDecimal(50.5)))
            Assert.assertThat(parsedStats.systemCPULoad, Is.`is`(BigDecimal(85.5)))
            Assert.assertThat(parsedStats.memoryCapacity, IsNull.nullValue())
            Assert.assertThat(parsedStats.memoryUsage, IsNull.nullValue())
        } catch (e:Exception) {
            Assert.fail(e.message);
        }
    }
    
    @Test
    fun parseBadStats() {
        val inputJSON = JSONObject()
        inputJSON.put("operatingSystem", "Windows 10")
        inputJSON.put("productVersion", "3.6.9")
        inputJSON.put("processCPULoad", 200.0)
        inputJSON.put("systemCPULoad", -200.0)
        inputJSON.put("memoryCapacity", -5)
        inputJSON.put("memoryUsage", -5)
        try {
            statsParserService.parseComputerStats(InputDTO<JSONObject>("abc123", inputJSON, Clock.systemUTC().instant()))
            Assert.fail("Expected a StatsParserException, yet none was raised")
        } catch (spe:StatsParserException) {
            // No-op - expected behavior
        }
    }
    
    @Test
    fun parseInvalidStats() {
        val inputJSON = JSONObject()
        inputJSON.put("operatingSystem", "Windows 10")
        inputJSON.put("productVersion", "3.6.9")
        inputJSON.put("processCPULoad", "Fail")
        inputJSON.put("systemCPULoad", 85.5)
        inputJSON.put("memoryCapacity", 1000000)
        inputJSON.put("memoryUsage", 100000)
        try {
            statsParserService.parseComputerStats(InputDTO<JSONObject>("abc123", inputJSON, Clock.systemUTC().instant()))
            Assert.fail("Expected a StatsParserException, yet none was raised")
        } catch (spe:StatsParserException) {
            // No-op - expected behavior
        }
    }
    
    @Test
    fun parseUnknownStats() {
        val inputJSON = JSONObject()
        inputJSON.put("operatingSystem", "Windows 10")
        inputJSON.put("computerType", "Acer")
        inputJSON.put("productVersion", "3.6.9")
        inputJSON.put("processCPULoad", 65.5)
        inputJSON.put("systemCPULoad", 85.5)
        inputJSON.put("memoryCapacity", 1000000)
        inputJSON.put("memoryUsage", 100000)
        try {
            val parsedStats = statsParserService.parseComputerStats(InputDTO<JSONObject>("abc123", inputJSON, Clock.systemUTC().instant()))
            Assert.assertThat(parsedStats.operatingSystem, Is.`is`("Windows 10"))
            Assert.assertThat(parsedStats.productVersion, Is.`is`("3.6.9"))
            Assert.assertThat(parsedStats.processCPULoad, Is.`is`(BigDecimal(65.5)))
            Assert.assertThat(parsedStats.systemCPULoad, Is.`is`(BigDecimal(85.5)))
            Assert.assertThat(parsedStats.memoryCapacity, Is.`is`(1000000L))
            Assert.assertThat(parsedStats.memoryUsage, Is.`is`(100000L))
        } catch (spe:StatsParserException) {
            Assert.fail(spe.message)
        }
    }
}
