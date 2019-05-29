package com.toqomo.settings

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Configuration.*
import com.natpryce.konfig.ConfigurationProperties
import java.io.File
import java.net.URI
import java.util.*
import kotlin.collections.HashMap
import com.codeborne.selenide.Selenide.sleep
import com.codeborne.selenide.WebDriverRunner
import org.openqa.selenium.Dimension
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.logging.Level
import com.codeborne.selenide.WebDriverRunner.getWebDriver
import org.apache.log4j.LogManager
import org.apache.log4j.Logger


object Driver {

    lateinit var service: ChromeDriverService
    lateinit var config: ConfigurationProperties

    fun setUpDriver() {
        config = Configs.loadProperties()
        Configuration().apply {
            savePageSource = config[Configs.save_page_source]
            screenshots = config[Configs.make_screenshot]
            browser = config[Configs.browser_name]
            baseUrl = config[Configs.base_url]
            startMaximized = true
        }
        if (config[Configs.is_grid]) {
            setUpDriverGrid(browser)
        } else {
            //setUpDriverLocal(browser)
        }
    }

    private fun setUpDriverGrid(browser: String) {
        val capabilities = DesiredCapabilities()
        val options = ChromeOptions()
        when (browser) {
            "chrome" -> {
                capabilities.apply {
                    browserName = browser
                    setCapability("chrome.switches", Arrays.asList("--disable-extensions"))
                    setCapability("enableVNC", true)
                    setCapability("enableVideo", false)
                }
                val prefs = HashMap<String, Any>()
                prefs["intl.accept_languages"] = "ru"
                options.apply {
                    addArguments("disable-infobars")
                    addArguments("--disable-save-password-bubble")
                    addArguments("test-type")
                    addArguments("start-fullscreen")
                    setExperimentalOption("prefs", prefs)
                    merge(capabilities)
                }
            }
            "firefox" -> {}
            "opera" -> {}
        }
        try {
            remote = config[Configs.remote_url]
            val remoteWebDriver: RemoteWebDriver = if (browser == "chrome") {
                RemoteWebDriver(URI.create(Configuration.remote).toURL(), options)
            } else {
                RemoteWebDriver(URI.create(Configuration.remote).toURL(), capabilities)
            }
            WebDriverRunner.setWebDriver(remoteWebDriver)
            if (browser != "opera") {
                remoteWebDriver.manage().window().size = Dimension(1920, 1080)
            }
        } catch (e: Exception) {
            throw RuntimeException("HUB is not running on server - '${Configuration.remote}'")
        }
    }

    private fun setUpDriverLocal(browser: String) {
        when (browser) {
            "chrome" -> {
                service = ChromeDriverService.Builder()
                    .usingDriverExecutable(File(setDriverLocalPath()))
                    .usingAnyFreePort()
                    .build()
                val options = ChromeOptions()
                options.apply {
                    addArguments("disable-infobars")
                    addArguments("--disable-save-password-bubble")
                    addArguments("test-type")
                    addArguments("start-fullscreen")
                }
                val logging = LoggingPreferences()
                logging.enable(LogType.BROWSER, Level.ALL)
                val capabilities = DesiredCapabilities()
                capabilities.apply {
                    setCapability(ChromeOptions.CAPABILITY, options)
                    setCapability("pageLoadStrategy", "none" )
                    setCapability(CapabilityType.LOGGING_PREFS, logging)
                }
                val prefs = HashMap<String, Any>()
                prefs["intl.accept_languages"] = "ru"
                options.setExperimentalOption("prefs", prefs)
                WebDriverRunner.setWebDriver(ChromeDriver(service, options))
            }
        }
    }

    fun tearDownDriver() {
        WebDriverRunner.getWebDriver().close()
    }

    private fun setDriverLocalPath(): String {
        val osName = System.getProperty("os.name")
        when {
            osName.startsWith("Mac") -> return "src/test/resources/chromedriver-mac"
            osName.startsWith("Windows") -> return "src/test/resources/chromedriver-windows.exe"
            osName.startsWith("Linux") -> return "src/test/resources/chromedriver-linux"
        }
        return ""
    }

    fun printLogs(log: Logger) {
        sleep(3000)
        val logEntries = getWebDriver().manage().logs().get(LogType.BROWSER)
        for (entry in logEntries) {
            log.error(Date(entry.timestamp).toString() + " " + entry.level + " " + entry.message)
        }
    }

}