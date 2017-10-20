package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.model.ComputerStats
import com.severett.kotlin_ras.model.LogFile

interface PersisterService {
    fun saveComputerStats(computerStats:ComputerStats)
    fun saveLogFile(logFile:LogFile)
}
