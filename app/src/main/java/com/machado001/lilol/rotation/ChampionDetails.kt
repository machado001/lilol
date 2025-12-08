package com.machado001.lilol.rotation

import android.widget.LinearLayout
import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.base.BaseView
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.view.SpellListItem

interface ChampionDetails {
    interface Presenter : BasePresenter {
        fun getRelatedChampions(relatedChampions: List<Champion>)
        suspend fun getChampionDetails(
            version: String,
            lang: String,
            championName: String,
        )
    }

    interface View : BaseView<Presenter> {
        fun setupRecyclerView(champions: ListChampionPair)
        fun showSuccess(champions: ListChampionPair)
        fun showErrorMessage()
        fun goToMoreDetailsScreen()
        fun setupChampionDetails(championDetail: com.machado001.lilol.common.model.data.ChampionDetails)
        fun showProgress(show: Boolean)
        fun showSpellList(spells: List<SpellListItem>)
        fun showSkinsList(skins: List<com.machado001.lilol.common.model.data.Skin>, championId: String)
        fun populateChampionsTips(
            tips: List<String>,
            lore: String,
            noTipsMessage: String,
            layout: LinearLayout
        )
    }
}