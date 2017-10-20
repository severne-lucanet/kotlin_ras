package com.severett.kotlin_ras.exception

open class StatsParserException(reason:String) : Exception("Error parsing computer statistics: $reason")
