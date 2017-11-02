package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO
import io.reactivex.SingleObserver
import org.json.JSONObject

interface StatProcessorService : SingleObserver<InputDTO<JSONObject>>
