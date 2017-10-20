package com.severett.kotlin_ras.model

import java.time.Instant
import javax.validation.constraints.NotNull

open class LogFile() : PersistenceEntity() {
    @NotNull var content:String? = null
    
    constructor(
            computerUuid:String,
            timestamp:Instant,
            content:String
    ) : this() {
        super.computerUuid = computerUuid
        super.timestamp = timestamp
        this.content = content
    }
}
