package com.machado001.lilol.rotation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.machado001.lilol.R
import com.machado001.lilol.common.Constants
import com.machado001.lilol.common.model.data.Champion
import com.squareup.picasso.Picasso

//name, roles, img
class AllChampionsAdapter(
    champions: List<Champion>,
    private val goToChampDetails: (String, String, String) -> Unit,
) :
    RecyclerView.Adapter<AllChampionsAdapter.AllChampionsViewHolder>() {

    private val allChampions = champions.toMutableList()

    fun updateData(newChampions: List<Champion>) {
        allChampions.clear()
        allChampions.addAll(newChampions)
        notifyDataSetChanged()
    }

    inner class AllChampionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(champion: Champion) {

            with(itemView) {
                findViewById<ImageView>(R.id.item_img_champion_card_rv).apply {
                    Picasso.get()
                        .load("${Constants.DATA_DRAGON_BASE_URL}cdn/img/champion/splash/${champion.id}_0.jpg")
                        .into(this)
                    contentDescription = champion.name
                }

                findViewById<TextView>(R.id.item_text_champion_name).apply {
                    text = champion.name
                }

                setOnClickListener {
                    goToChampDetails(
                        champion.id,
                        champion.id,
                        champion.version
                    )
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AllChampionsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_all_champions_rv, parent, false)
        )

    override fun getItemCount(): Int = allChampions.size

    override fun onBindViewHolder(holder: AllChampionsViewHolder, position: Int) =
        holder.bind(allChampions[position])
}
