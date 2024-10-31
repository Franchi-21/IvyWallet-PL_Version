package com.ivy.core.ui.time.picker.date

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.common.androidtest.MainCoroutineRule
import com.ivy.common.androidtest.TimeProviderFake
import com.ivy.core.ui.time.picker.date.data.PickerDay
import com.ivy.core.ui.time.picker.date.data.PickerMonth
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class DatePickerViewModelTest : IvyAndroidTest() {
    private lateinit var datePickerViewModel: DatePickerViewModel

    override fun setUp() {
        super.setUp()
        datePickerViewModel = DatePickerViewModel(
            appContext = context,
            timeProvider = timeProvider
        )
    }

    @Test
    fun `If a invalid date is entered, throw exception`() = runTest {
        datePickerViewModel
    }
}