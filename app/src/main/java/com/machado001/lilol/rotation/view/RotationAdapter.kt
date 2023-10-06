package com.machado001.lilol.rotation.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.machado001.lilol.R
import com.machado001.lilol.common.model.data.Champion
import com.squareup.picasso.Picasso

class RotationAdapter(
    private val rotations: List<Champion> //just a list of champions
) : RecyclerView.Adapter<RotationAdapter.RotationViewHolder>() {


    class RotationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(champion: Champion) {
            itemView.findViewById<ImageView>(R.id.image_item_champion).apply {
                Picasso.get().load(champion.image).into(this)
                contentDescription = champion.name
            }

            itemView.findViewById<TextView>(R.id.text_item_champion_name).apply {
                text = champion.name
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

