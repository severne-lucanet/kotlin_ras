package com.severett.kotlin_ras

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration
import org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.boot.autoconfigure.EnableAutoConfiguration

@ComponentScan
@Configuration
@Import(
        DispatcherServletAutoConfiguration::class,
        EmbeddedServletContainerAutoConfiguration::class,
        ErrorMvcAutoConfiguration::class,
        HttpEncodingAutoConfiguration::class,
        HttpMessageConvertersAutoConfiguration::class,
        JacksonAutoConfiguration::class,
        PropertyPlaceholderAutoConfiguration::class,
        ServerPropertiesAutoConfiguration::class,
        ValidationAutoConfiguration::class,
        WebMvcAutoConfiguration::class
)
open class MainApp

fun main(args : Array<String>) {
    SpringApplication.run(MainApp::class.java, *args)
}
