/**
 * Copyright 2017 Severn Everett
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.severett.kotlin_ras

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration
import org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

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
