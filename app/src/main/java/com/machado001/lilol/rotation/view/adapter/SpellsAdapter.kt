package com.machado001.lilol.rotation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.machado001.lilol.R
import com.machado001.lilol.common.Constants
import com.machado001.lilol.common.view.SpellListItem
import com.squareup.picasso.Picasso

class SpellsAdapter(
    private val spells: List<SpellListItem>,
    private val gameVersion: String,
) :
    RecyclerView.Adapter<SpellsAdapter.SpellsViewHolder>() {

    inner class SpellsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(spell: SpellListItem) {
            itemView.findViewById<ImageView>(R.id.img_champion_spell_item).apply {

                Picasso.get()
                    .load("${Constants.DATA_DRAGON_BASE_URL}cdn/$gameVersion/img/spell/${spell.spellImageUri}")
                    .placeholder(android.R.drawable.screen_background_dark_transparent)
                    .into(this)

                contentDescription = spell.id
            }

            itemView.findViewById<TextView>(R.id.tv_champion_spell_item).apply {
                text = spell.keyboardKey.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SpellsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_spell_rv, parent, false)
        )

    override fun getItemCount() = spells.size

    override fun onBindViewHolder(holder: SpellsViewHolder, position: Int) {
        holder.bind(spells[position])
    }

}
