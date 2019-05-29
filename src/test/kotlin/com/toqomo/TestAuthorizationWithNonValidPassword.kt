package com.toqomo

import com.codeborne.selenide.Configuration.baseUrl
import com.codeborne.selenide.Selenide
import com.toqomo.moduls.SignIn
import com.toqomo.settings.Driver
import org.apache.log4j.Logger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestAuthorizationWithNonValidPassword {

    private val log = Logger.getLogger(TestAuthorizationWithNonValidPassword::class.java)

    @BeforeEach
    fun setUp() {
        Driver.setUpDriver()
        Selenide.open(baseUrl)
    }

    @Test
    fun testAuthorizationWithNonValidPassword() {
        SignIn.setValueInputPhone("71234567890")
        SignIn.setValueInputPassword("123456Qwerty")
        SignIn.clickButtonSubmit()
    }

    @AfterEach
    fun tearDown() {
        Driver.printLogs(log)
        Driver.tearDownDriver()
    }
}