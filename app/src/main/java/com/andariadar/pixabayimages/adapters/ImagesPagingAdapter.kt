package com.andariadar.pixabayimages.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andariadar.pixabayimages.R
import com.andariadar.pixabayimages.databinding.ItemImageBinding
import com.andariadar.pixabayimages.model.Image
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ImagesPagingAdapter: PagingDataAdapter<Image, ImagesPagingAdapter.ImagesViewHolder>(ImageComparator) {
    inner class ImagesViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            binding.apply {
                user.text = image.user
                likes.text = image.likes.toString()
                Glide.with(itemView)
                    .load(image.webformatURL)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageMain)

            }
        }
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagesViewHolder(binding)
    }

    object ImageComparator: DiffUtil.ItemCallback<Image>(){
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }
}
