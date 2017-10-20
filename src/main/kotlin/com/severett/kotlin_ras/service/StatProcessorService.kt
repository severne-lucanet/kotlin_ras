package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO
import org.json.JSONObject
import reactor.bus.Event
import reactor.fn.Consumer

interface StatProcessorService : Consumer<Event<InputDTO<JSONObject>>>
