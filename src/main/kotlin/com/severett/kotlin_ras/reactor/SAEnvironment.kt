package com.severett.kotlin_ras.reactor

import org.springframework.stereotype.Component
import reactor.Environment
import javax.annotation.PreDestroy

@Component
open class SAEnvironment : Environment() {
    init {
        assignErrorJournal()
    }
    
    @PreDestroy
    fun shutdownEnv() {
        shutdown()
    }
}
