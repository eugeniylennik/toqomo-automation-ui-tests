package com.toqomo.moduls

import com.codeborne.selenide.Selenide.`$`
import org.openqa.selenium.By

object SignIn  {

    private val inputPhone = `$`(By.name("login"))
    private val inputPassword = `$`(By.name("password"))
    private val buttonSubmit = `$`(By.xpath("//button[@type='submit']"))

    fun setValueInputPhone(phone: String) {
        inputPhone.value = phone
    }

    fun setValueInputPassword(password: String) {
        inputPassword.value = password
    }

    fun clickButtonSubmit() {
        buttonSubmit.click()
    }

}