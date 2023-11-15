package com.machado001.lilol.rotation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.machado001.lilol.R
import com.machado001.lilol.common.Constants.DATA_DRAGON_BASE_URL
import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.common.model.data.Champion
import com.squareup.picasso.Picasso

class RotationAdapter(
    private val rotations: ListChampionPair,
    private val goToChampionDetails: (String, String, String) -> Unit,
) : RecyclerView.Adapter<RotationAdapter.RotationViewHolder>() {

    inner class RotationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(championPair: Map.Entry<String, Champion>) {
            itemView.findViewById<ImageView>(R.id.image_item_champion).apply {

                Picasso.get()
                    .load("${DATA_DRAGON_BASE_URL}cdn/${championPair.value.version}/img/champion/${championPair.key}.png")
                    .placeholder(android.R.drawable.screen_background_dark_transparent)
                    .into(this)

                contentDescription = championPair.value.name

                setOnClickListener {
                    goToChampionDetails(
                        championPair.value.key,
                        championPair.key,
                        championPair.value.version
                    )
                }
            }
            itemView.findViewById<TextView>(R.id.text_item_champion_name).apply {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RotationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rotation_rv, parent, false)
        )
    override fun onBindViewHolder(holder: RotationViewHolder, position: Int) {
        holder.bind(rotations[position])
    }
    override fun getItemCount(): Int = rotations.size
}

