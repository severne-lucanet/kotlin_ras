package com.severett.kotlin_ras.util

import java.io.File

class TestConstants {
    companion object {
        val GOOD_RESOURCES_DIRECTORY = File("src/test/resources/good_logs")
        val BAD_RESOURCES_DIRECTORY = File("src/test/resources/bad_logs")
    }
}