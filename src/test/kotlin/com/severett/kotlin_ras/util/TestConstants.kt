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
package com.severett.kotlin_ras.util

import java.io.File

class TestConstants {
    companion object {
        @JvmField
        val GOOD_RESOURCES_DIRECTORY = File("src/test/resources/good_logs")
        @JvmField
        val BAD_RESOURCES_DIRECTORY = File("src/test/resources/bad_logs")
    }
}