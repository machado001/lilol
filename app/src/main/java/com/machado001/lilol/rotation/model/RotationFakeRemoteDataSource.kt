package com.machado001.lilol.rotation.model

import android.os.Handler
import android.os.Looper
import com.machado001.lilol.common.base.RequestCallback
import com.machado001.lilol.rotation.model.retrofit.RotationRemoteDataSource
import io.github.serpro69.kfaker.Faker

class RotationFakeRemoteDataSource : RotationDataSource {

    private val randomId = Faker().random.nextInt()
    private val minimalLevel = 10


    override suspend fun fetchRotations(callback: RequestCallback<Rotations>) {
        Handler(Looper.getMainLooper()).postDelayed({
            val freeChampionIds = mutableListOf<Int>().apply {
                repeat(10) {
                    this.add(randomId)
                }
            }
            val freeChampionIdsForNewPlayers = mutableListOf<Int>().apply {
                repeat(10) {
                    this.add(randomId)
                }
            }

            val rotations = Rotations(
                freeChampionIds,
                freeChampionIdsForNewPlayers,
                minimalLevel
            )

            callback.onSuccess(rotations)


            callback.onComplete()

        }, 1500)
    }
}