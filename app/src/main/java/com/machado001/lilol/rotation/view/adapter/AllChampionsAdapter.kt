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
    private val allChampions: List<Champion>,
    private val goToChampDetails: (String, String, String) -> Unit,
) :
    RecyclerView.Adapter<AllChampionsAdapter.AllChampionsViewHolder>() {
    inner class AllChampionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(champion: Champion) {

            with(itemView) {
                findViewById<ImageView>(R.id.item_img_champion_card_rv).apply {
                    Picasso.get()
                        .load("${Constants.DATA_DRAGON_BASE_URL}cdn/img/champion/splash/${champion.id}_0.jpg")
                        .into(this)
                }

                findViewById<TextView>(R.id.item_text_champion_name).apply {
                    text = champion.name
                }

                findViewById<TextView>(R.id.item_text_role_one).apply {
                    text = champion.tags.first()
                }

                findViewById<TextView>(R.id.item_text_role_two).apply {
                    text =
                        if (champion.tags.last() == champion.tags.first()) " " else champion.tags.last()
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