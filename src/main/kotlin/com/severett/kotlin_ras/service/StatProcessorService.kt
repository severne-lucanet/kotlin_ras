package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO
import org.json.JSONObject

interface StatProcessorService {
    fun processStats(inputDTO:InputDTO<JSONObject>)
}
