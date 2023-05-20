package com.yhezra.storyapps.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yhezra.storyapps.data.remote.response.StoryResponse
import com.yhezra.storyapps.databinding.ItemStoryBinding
import com.bumptech.glide.Glide

class ListStoryAdapter(
    private val listStoryResponse: List<StoryResponse>,
    private val onClick: (StoryResponse) -> Unit
) : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvItemName.text = listStoryResponse[holder.adapterPosition].name
        Glide.with(holder.itemView)
            .load(listStoryResponse[holder.adapterPosition].photoUrl)
            .into(holder.binding.ivItemStory)
        holder.binding.tvItemDescription.text = listStoryResponse[holder.adapterPosition].description

        holder.itemView.setOnClickListener {
            onClick(listStoryResponse[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listStoryResponse.size
}