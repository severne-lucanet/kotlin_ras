package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO
import com.severett.kotlin_ras.model.ComputerStats
import org.json.JSONObject

interface StatsParserService {
    fun parseComputerStats(inputDTO:InputDTO<JSONObject>) : ComputerStats
}
