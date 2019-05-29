package com.toqomo.settings

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.Key
import com.natpryce.konfig.booleanType
import com.natpryce.konfig.stringType
import java.io.File

object Configs {

    val browser_name = Key("browser_name", stringType)
    val is_grid = Key("is_grid", booleanType)
    val base_url = Key("base_url", stringType)
    val remote_url = Key("remote_url", stringType)
    val save_page_source = Key("save_page_source", booleanType)
    val make_screenshot = Key("make_screenshot", booleanType)

    fun loadProperties(): ConfigurationProperties {
        return ConfigurationProperties.fromFile(File("config.properties"))
    }
}


