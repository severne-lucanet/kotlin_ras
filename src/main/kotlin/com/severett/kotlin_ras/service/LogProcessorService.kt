package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO

interface LogProcessorService {
    fun processLogFile(inputDTO:InputDTO<ByteArray>)
}
