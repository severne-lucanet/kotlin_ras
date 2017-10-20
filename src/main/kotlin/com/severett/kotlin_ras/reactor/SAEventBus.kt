package com.severett.kotlin_ras.reactor

import org.springframework.stereotype.Component
import reactor.Environment
import reactor.bus.EventBus

@Component
open class SAEventBus(val environment : SAEnvironment) : EventBus(environment.getDispatcher(Environment.THREAD_POOL))
