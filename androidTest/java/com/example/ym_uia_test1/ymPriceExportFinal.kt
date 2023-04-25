package com.example.ym_uia_test1

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.Thread.sleep

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class YandexMarketTestFinal () {
    @Test
    @Throws(Exception::class)
    open fun csvSearch() {

        val input = this.javaClass.classLoader.getResourceAsStream("Temp.csv")
        val output = mutableListOf<String>()
        var i = 0
        val csvList = readCsv(input)
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        for (line in csvList) {
            val rand: Int = (10000..15000).random()
            sleep(rand.toLong())
            val element = line.Name
            val search: UiObject =
                mDevice.findObject(UiSelector().resourceId("ru.beru.android:id/searchRequestView"))
            search.click()
            val searchText: UiObject =
                mDevice.findObject(UiSelector().resourceId("ru.beru.android:id/viewSearchAppBarLayoutInput"))
            searchText.setText(element)
            val searchIcon: UiObject =
                mDevice.findObject(UiSelector().resourceId("ru.beru.android:id/viewSearchAppBarLayoutSearchIcon"))
            searchIcon.click()
            val image: UiObject =
                mDevice.findObject(UiSelector().resourceId("ru.beru.android:id/backgroundImageView"))
            while (!image.exists())
                sleep(1000)
            image.click()
            val sellersLink = mDevice.findObject(
                UiSelector().resourceId("ru.beru.android:id/linkButton")
                    .textContains("продавцов"))
            val statsTitle =
                mDevice.findObject(
                    UiSelector().resourceId("ru.beru.android:id/linkButton")
                        .textContains("Стать продавцом"))
            val sellersButton = mDevice.findObject(UiSelector().resourceId(""))
            var price0: UiObject =
                mDevice.findObject(UiSelector().resourceId("ru.beru.android:id/basePriceTextView"))
            var firstPriceBool : Boolean
            firstPriceBool = false
            do {
                mDevice.drag(500, 1000, 500, 800, 6)
                mDevice.drag(500, 500, 500, 300, 6)
                if ((price0.exists()) and (!firstPriceBool)) {
                    firstPriceBool = true
                    output.add(element + ": " + price0.text)
                }
            } while (!sellersLink.exists() and !sellersButton.exists() and !statsTitle.exists())
            if (sellersLink.exists()) {
                mDevice.drag(500, 500, 500, 300, 6)
                sellersLink.click()
                sleep(rand.toLong())
                output[i] = priceList(element)
            } else if (sellersButton.exists()) {
                mDevice.drag(500, 500, 500, 300, 6)
                sellersButton.click()
                sleep(rand.toLong())
                output[i] = priceList(element)
            }
            val navMain: UiObject =
                mDevice.findObject(UiSelector().resourceId("ru.beru.android:id/nav_main"))
            navMain.click()
            println(output[i])
            i+=1
        }
        var dir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
        FileOutputStream(File(dir, "Output1.txt")).apply {
            writeFile(output)
        }
    }


    fun priceList(title: String): String {
        var output: String = title
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val navPanel: UiObject =
            mDevice.findObject(UiSelector().resourceId("ru.beru.android:id/bottomNavigationBar"))
        val price0: UiObject =
            mDevice.findObject(
                (UiSelector().resourceId("ru.beru.android:id/actualPriceView").instance(0))
            )
        var price1: UiObject =
            mDevice.findObject(
                (UiSelector().resourceId("ru.beru.android:id/actualPriceView").instance(1))
            )
        var price2: UiObject =
            mDevice.findObject(
                (UiSelector().resourceId("ru.beru.android:id/actualPriceView").instance(2))
            )
        var time_waiting = 0
        while (!price0.exists()) {
            sleep(1000)
            time_waiting+=1
            if (time_waiting==25)
            {
                var back: UiObject =
                    mDevice.findObject((UiSelector().className("android.widget.ImageButton")))
                back.click()
                val image: UiObject =
                    mDevice.findObject(UiSelector().resourceId("ru.beru.android:id/backgroundImageView"))
                image.click()
            }
        }
        mDevice.pressDPadDown()
        mDevice.pressDPadDown()
        mDevice.pressDPadDown()
        mDevice.pressDPadDown()
        if (price0.exists())
            output += ": " + price0.text
        if (price1.exists())
            output += ", " + price1.text
        while (!navPanel.isFocused) {
            if (price2.exists()) {
                price2 =
                    mDevice.findObject(
                        (UiSelector().resourceId("ru.beru.android:id/actualPriceView").instance(2))
                    )
                output += ", " + price2.text
            } else if (price1.exists()) {
                price1 =
                    mDevice.findObject(
                        (UiSelector().resourceId("ru.beru.android:id/actualPriceView").instance(1))
                    )
                output += ", " + price1.text
            }
            mDevice.pressDPadDown()
        }
        return (output)
    }

    fun OutputStream.writeFile(input: List<String>) {
        val writer = bufferedWriter()
        input.forEach {
            writer.write(it)
            writer.newLine()
        }
        writer.flush()
    }

    data class Product(
        val Name: String
    )

    fun readCsv(inputStream: InputStream): List<Product> {
        val reader = inputStream.bufferedReader()
        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                Product(it)
            }.toList()
    }
}