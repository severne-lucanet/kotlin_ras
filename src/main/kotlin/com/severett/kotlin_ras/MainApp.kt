package com.severett.kotlin_ras

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class MainApp {
}

fun main(args : Array<String>) {
    SpringApplication.run(MainApp::class.java, *args)
}
