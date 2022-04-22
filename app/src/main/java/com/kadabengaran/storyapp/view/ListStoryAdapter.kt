package com.kadabengaran.storyapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.databinding.ItemStoriesBinding
import com.kadabengaran.storyapp.service.database.StoryEntity
import com.kadabengaran.storyapp.service.model.StoryItem
import com.kadabengaran.storyapp.utils.withDateFormat

class ListStoryAdapter:

    PagingDataAdapter<StoryEntity, ListStoryAdapter.ViewHolder>(DIFF_CALLBACK){

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class ViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userItem: StoryEntity) {
            with(binding) {
                Glide.with(itemView)
                    .load(userItem.photoUrl)
                    .dontTransform()
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error_image)
                    .into(imgStory)
                tvUsername.text = userItem.name
                tvItemDate.text = itemView.context.getString(
                    R.string.dateFormat,
                    userItem.createdAt.withDateFormat()
                )
                binding.root.setOnClickListener {
                    onItemClickCallback.onItemClicked(userItem, cardItem)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryEntity, cardItem: CardView)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
