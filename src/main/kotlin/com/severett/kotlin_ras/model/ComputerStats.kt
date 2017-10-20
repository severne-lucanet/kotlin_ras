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
