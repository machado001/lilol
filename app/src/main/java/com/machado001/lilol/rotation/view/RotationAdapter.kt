package com.machado001.lilol.rotation.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.machado001.lilol.R
import com.machado001.lilol.rotation.model.Champion

class RotationAdapter(
    private val rotations: MutableList<Champion> //just a list of champions
) : RecyclerView.Adapter<RotationAdapter.RotationViewHolder>() {


    class RotationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(champion: Champion) {
            itemView.findViewById<ImageView>(R.id.image_item_champion).apply {
                setImageResource(champion.image)
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