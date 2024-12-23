package com.machado001.lilol.common.background

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.machado001.lilol.common.background.doubles.fakes.FakeContainer
import com.machado001.lilol.rotation.model.background.RotationWorker
import com.machado001.lilol.rotation.model.background.RotationWorkerFactory
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RotationWorkerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .executeShellCommand("pm grant com.machado001.lilol android.permission.POST_NOTIFICATIONS")
    }

    private val fakeRotationRepository = FakeContainer()

    @Test
    fun testRotationWorker() {

        val worker = TestListenableWorkerBuilder<RotationWorker>(context)
            .setWorkerFactory(RotationWorkerFactory(fakeRotationRepository))
            .build()


        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }
}

