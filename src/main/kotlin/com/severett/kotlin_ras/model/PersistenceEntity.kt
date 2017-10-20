package com.severett.kotlin_ras.model

import java.time.Instant
import javax.validation.constraints.NotNull

abstract class PersistenceEntity() {
    @NotNull var computerUuid:String? = null
    @NotNull var timestamp:Instant? = null
}
