package com.kadabengaran.storyapp.view

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.databinding.ItemStoriesBinding
import com.kadabengaran.storyapp.service.model.StoryItem
import com.kadabengaran.storyapp.utils.withDateFormat

class ListStoryAdapter (private val data: MutableList<StoryItem>
) : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setData(items: List<StoryItem>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userItem: StoryItem) {
            with(binding) {
                Glide.with(itemView)
                    .load(userItem.photoUrl)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error_image)
                    .into(imgStory)
                tvUsername.text = userItem.name
                tvItemDate.text = itemView.context.getString(R.string.dateFormat, userItem.createdAt.withDateFormat())
//                imgStory.transitionName = userItem.id
                cardItem.transitionName = "card"+userItem.id
                binding.root.setOnClickListener {
                    onItemClickCallback.onItemClicked(userItem, binding)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryItem, binding: ItemStoriesBinding)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}
