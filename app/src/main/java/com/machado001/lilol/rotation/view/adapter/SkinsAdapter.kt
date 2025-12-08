package com.machado001.lilol.rotation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machado001.lilol.common.Constants
import com.machado001.lilol.common.model.data.Skin
import com.machado001.lilol.databinding.ItemSkinCarouselBinding
import com.squareup.picasso.Picasso

class SkinsAdapter(
    private val skins: List<Skin>,
    private val championId: String
) : RecyclerView.Adapter<SkinsAdapter.SkinViewHolder>() {

    inner class SkinViewHolder(private val binding: ItemSkinCarouselBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(skin: Skin) {
            binding.apply {
                val skinName = if (skin.name == "default") "Default" else skin.name
                txtSkinName.text = skinName
                
                val imageUrl = "${Constants.DATA_DRAGON_BASE_URL}cdn/img/champion/splash/${championId}_${skin.num}.jpg"
                
                Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(imgSkin)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkinViewHolder {
        val binding = ItemSkinCarouselBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SkinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkinViewHolder, position: Int) {
        holder.bind(skins[position])
    }

    override fun getItemCount(): Int = skins.size
}
