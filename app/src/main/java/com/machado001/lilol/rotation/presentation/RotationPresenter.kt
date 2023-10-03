package com.machado001.lilol.rotation.presentation

import com.machado001.lilol.R
import com.machado001.lilol.common.base.RequestCallback
import com.machado001.lilol.common.extensions.getCurrentDateTime
import com.machado001.lilol.common.extensions.toString
import com.machado001.lilol.rotation.Rotation
import com.machado001.lilol.rotation.model.Champion
import com.machado001.lilol.rotation.model.RotationRepository
import com.machado001.lilol.rotation.model.Rotations
import io.github.serpro69.kfaker.Faker

class RotationPresenter(
    private val repository: RotationRepository,
    private var view: Rotation.View?

) : Rotation.Presenter {

    override suspend fun fetchRotations() {
        view?.showProgress(true)

        repository.fetchRotations(object : RequestCallback<Rotations> {
            override fun onSuccess(data: Rotations) {
                val date = getCurrentDateTime()
                val dateInString = date.toString("MMMM, dd")
                val freeChampions = data.freeChampionIds.map {
                    Champion(
                        it.toString(),
                        Faker().heroes.names(),
                        R.drawable.briar
                    )
                }
                val freeChampionsForNewPlayers =
                    data.freeChampionIdsForNewPlayers.map {
                        Champion(
                            it.toString(),
                            Faker().heroes.names(),
                            R.drawable.zed
                        )
                    }

                view?.showSuccess(
                    freeChampions,
                    freeChampionsForNewPlayers,
                    data.maxNewPlayerLevel,
                    dateInString
                )
            }

            override fun onFailure(message: String) {
                view?.showFailureMessage()
            }

            override fun onComplete() {
                view?.showProgress(false)
            }
        })
    }

    override fun onDestroy() {
        view = null
    }
}