package com.example.photo.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.photo.R
import com.example.photo.databinding.ItemPhotoBinding
import com.example.photo.domain.model.Photo

class PhotoAdapter(
    private val onPhotoClick: (Photo) -> Unit,
    private val onFavoriteClick: (Photo) -> Unit
) : ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding, onPhotoClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PhotoViewHolder(
        private val binding: ItemPhotoBinding,
        private val onPhotoClick: (Photo) -> Unit,
        private val onFavoriteClick: (Photo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.photoTitle.text = photo.title
            val description = photo.description.ifBlank {
                binding.photoDescription.context.getString(R.string.detail_no_description)
            }
            binding.photoDescription.text = description
            binding.photoMeta.text =
                binding.photoMeta.context.getString(
                    R.string.photo_meta_template,
                    photo.likes,
                    photo.resolutionText
                )
            binding.photoThumb.load(photo.thumbUrl) {
                crossfade(true)
            }
            val (icon, contentDescription) = if (photo.isFavorite) {
                R.drawable.ic_favorite_filled to binding.favoriteButton.context.getString(R.string.action_unlike)
            } else {
                R.drawable.ic_favorite_outline to binding.favoriteButton.context.getString(R.string.action_like)
            }
            binding.favoriteButton.setImageResource(icon)
            binding.favoriteButton.contentDescription = contentDescription
            binding.root.setOnClickListener { onPhotoClick(photo) }
            binding.favoriteButton.setOnClickListener { onFavoriteClick(photo) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem == newItem
    }
}
