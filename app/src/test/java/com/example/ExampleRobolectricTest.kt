package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CurrencyHelper
import com.example.data.model.StudentEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("KasKelas PRO", appName)
    }

    @Test
    fun `currency helper formats correctly`() {
        val formatted = CurrencyHelper.formatRupiah(50000L)
        assertTrue(formatted.contains("50.000"))
    }

    @Test
    fun `student unpaid weeks helper returns correct string`() {
        val student = StudentEntity(
            name = "Test Siswa",
            absen = 1,
            gender = "L",
            w1 = true,
            w2 = false,
            w3 = false,
            w4 = true
        )
        assertEquals(2, student.unpaidWeeksCount)
        assertEquals("M2, M3", student.unpaidWeeksString)
    }
}
