package com.severett.kotlin_ras.service

import com.severett.kotlin_ras.dto.InputDTO
import io.reactivex.SingleObserver

interface LogProcessorService : SingleObserver<InputDTO<ByteArray>>
