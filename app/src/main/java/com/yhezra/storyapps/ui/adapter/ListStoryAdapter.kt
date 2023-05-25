package com.yhezra.storyapps.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yhezra.storyapps.databinding.ItemStoryBinding
import com.bumptech.glide.Glide
import com.yhezra.storyapps.data.local.entity.StoryEntity

class ListStoryAdapter(private val onClick: (StoryEntity, ActivityOptionsCompat) -> Unit) :
    PagingDataAdapter<StoryEntity, ListStoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.binding.tvItemName.text = data.name
            Glide.with(holder.itemView)
                .load(data.photoUrl)
                .into(holder.binding.ivItemStory)
            holder.binding.tvItemDescription.text = data.description

            holder.itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        holder.itemView.context as Activity,
                        Pair(holder.binding.ivItemStory, "photo"),
                        Pair(holder.binding.tvItemName, "name"),
                        Pair(holder.binding.tvItemDescription, "description")
                    )
                onClick(data, optionsCompat)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}