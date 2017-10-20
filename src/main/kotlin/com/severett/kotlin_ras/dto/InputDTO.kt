package com.severett.kotlin_ras.dto

import java.time.Instant

data class InputDTO<PayloadType>(val computerUuid:String, val payload:PayloadType, val timestamp:Instant)
