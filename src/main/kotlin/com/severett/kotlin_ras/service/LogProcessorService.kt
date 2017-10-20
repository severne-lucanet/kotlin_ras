package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO
import reactor.bus.Event
import reactor.fn.Consumer

interface LogProcessorService : Consumer<Event<InputDTO<ByteArray>>> {
}
