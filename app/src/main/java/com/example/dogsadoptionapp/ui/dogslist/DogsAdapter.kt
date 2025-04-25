package com.example.dogsadoptionapp.ui.dogslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.databinding.ItemDogBinding

class DogsAdapter(
    val onItemClick: (Dog) -> Unit,
    val onDeleteClick: (Dog) -> Unit,
    val onEditClick: (Dog) -> Unit
) : ListAdapter<Dog, DogsAdapter.DogViewHolder>(DogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = ItemDogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dog = getItem(position)
        holder.bind(dog)
    }

    inner class DogViewHolder(private val binding: ItemDogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dog: Dog) {
            binding.dogName.text = dog.name
            binding.dogBreed.text = dog.breed
            Glide.with(itemView.context).load(dog.imageUri).into(binding.dogImage)

            binding.root.setOnClickListener { onItemClick(dog) }
            binding.deleteButton.setOnClickListener { onDeleteClick(dog) }
            binding.editButton.setOnClickListener { onEditClick(dog) }
        }
    }
}

class DogDiffCallback : DiffUtil.ItemCallback<Dog>() {
    override fun areItemsTheSame(oldItem: Dog, newItem: Dog): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Dog, newItem: Dog): Boolean = oldItem == newItem
}