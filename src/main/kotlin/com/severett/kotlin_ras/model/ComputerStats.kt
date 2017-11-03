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
package com.severett.kotlin_ras.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.validator.constraints.Range
import java.math.BigDecimal
import java.time.Instant
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@JsonIgnoreProperties(ignoreUnknown = true)
open class ComputerStats() : PersistenceEntity() {
    @NotNull var operatingSystem:String? = null
    @NotNull var productVersion:String? = null
    @NotNull @Range(min = 0, max = 100) var processCPULoad:BigDecimal? = null
    @Range(min = 0, max = 100) var systemCPULoad:BigDecimal? = null
    @Min(value = 0) var memoryCapacity:Long? = null
    @Min(value = 0) var memoryUsage:Long? = null
    
    constructor (
            computerUuid:String,
            timestamp:Instant,
            operatingSystem:String,
            processCPULoad:BigDecimal,
            systemCPULoad:BigDecimal,
            memoryCapacity:Long,
            memoryUsage:Long
    ) : this() {
        super.computerUuid = computerUuid
        super.timestamp = timestamp
        this.operatingSystem = operatingSystem
        this.productVersion = productVersion
        this.processCPULoad = processCPULoad
        this.systemCPULoad = systemCPULoad
        this.memoryCapacity = memoryCapacity
        this.memoryUsage = memoryUsage
    }
}
