package com.andariadar.pixabayimages.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andariadar.pixabayimages.databinding.LoadStateFooterViewItemBinding

class ImagesLoadStateAdapter(private val retry: () -> Unit): LoadStateAdapter<ImagesLoadStateAdapter.LoadStateViewHolder>() {
    inner class LoadStateViewHolder(private val binding: LoadStateFooterViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState !is LoadState.Loading
                errorMessage.isVisible = loadState !is LoadState.Loading

                if (loadState is LoadState.Error) {
                    binding.errorMessage.text = loadState.error.localizedMessage
                }
            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = LoadStateFooterViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }
}