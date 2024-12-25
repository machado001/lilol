package com.machado001.lilol.common.background

import android.app.Instrumentation
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.machado001.lilol.common.background.doubles.fakes.FakeContainer
import com.machado001.lilol.rotation.model.background.RotationWorker
import com.machado001.lilol.rotation.model.background.RotationWorkerFactory
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RotationWorkerTest {
    private lateinit var context: Context
    private val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        grantNotificationPermission()
    }

    private val fakeContainer = FakeContainer()

    private fun grantNotificationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            instrumentation.uiAutomation.grantRuntimePermission(
                context.packageName,
                "android.permission.POST_NOTIFICATIONS"
            )
        } else {
            Unit
        }

    @Test
    fun shouldTriggerNotification_whenLocalAndNetworkDataDiffers() {

        val worker = TestListenableWorkerBuilder<RotationWorker>(context)
            .setWorkerFactory(RotationWorkerFactory(fakeContainer))
            .build()


        runBlocking {
            fakeContainer.rotationRepository.run {
                localRotations.collect { assertTrue(it != fetchRemoteRotations().toString()) }
            }
            val result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }
}

