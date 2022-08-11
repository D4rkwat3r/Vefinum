package com.liquid.vefinum.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.liquid.vefinum.R
import com.liquid.vefinum.databinding.CommunityItemBinding
import com.liquid.vefinum.model.Community
import com.liquid.vefinum.util.loadImage
import kotlinx.serialization.json.*

typealias Handler = (Int) -> Unit

class CommunityListAdapter(val communities: List<Community>, var onClick: Handler = {}) : RecyclerView.Adapter<CommunityListAdapter.CommunityVH>() {

    inner class CommunityVH(val binding: CommunityItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                binding.root.post {
                    onClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<CommunityItemBinding>(inflater, R.layout.community_item, parent, false)
        return CommunityVH(binding)
    }

    override fun onBindViewHolder(holder: CommunityVH, position: Int) {
        holder.binding.name.text = communities[position].name
        loadImage(holder.binding.icon, communities[position].icon, round = true, changeBrightness = false)
        if (communities[position].cover != null) {
            loadImage(holder.binding.cover, communities[position].cover!!, round = false, changeBrightness = true)
        } else {
            holder.binding.cover.setImageDrawable(ColorDrawable(Color.parseColor(communities[position].themeColor)))
        }
    }

    override fun getItemCount(): Int = communities.size

}