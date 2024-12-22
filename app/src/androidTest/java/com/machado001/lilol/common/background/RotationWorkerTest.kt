package com.machado001.lilol.common.background

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
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
    }

    private val fakeRotationRepository = FakeContainer()

    @Test
    fun testRotationWorker() {

        val data =
            Data.Builder()
                .putString("local", "Rotations(freeChampionIds=[10, 20, 30, 40, 50, 6, 7, 8], freeChampionIdsForNewPlayers=[9, 10, 11, 12, 13, 14, 15, 16], maxNewPlayerLevel=30)")
                .build()

        val worker = TestListenableWorkerBuilder<RotationWorker>(context)
            .setWorkerFactory(RotationWorkerFactory(fakeRotationRepository))
            .setInputData(data)
            .build()


        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }
}

