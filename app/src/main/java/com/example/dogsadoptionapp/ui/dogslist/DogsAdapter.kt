package com.example.dogsadoptionapp.ui.dogslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Dog

class DogsAdapter(
    val onItemClick: (Dog) -> Unit,
    val onDeleteClick: (Dog) -> Unit,
    val onEditClick: (Dog) -> Unit
) : ListAdapter<Dog, DogsAdapter.DogViewHolder>(DogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dog = getItem(position)
        holder.bind(dog)
    }

    inner class DogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dogName: TextView = view.findViewById(R.id.dogName)
        private val dogBreed: TextView = view.findViewById(R.id.dogBreed)
        private val dogImage: ImageView = view.findViewById(R.id.dogImage)
        private val deleteBtn: ImageButton = view.findViewById(R.id.deleteButton)
        private val editBtn: ImageButton = view.findViewById(R.id.editButton)

        fun bind(dog: Dog) {
            dogName.text = dog.name
            dogBreed.text = dog.breed
            Glide.with(itemView.context).load(dog.imageUri).into(dogImage)

            itemView.setOnClickListener { onItemClick(dog) }
            deleteBtn.setOnClickListener { onDeleteClick(dog) }
            editBtn.setOnClickListener { onEditClick(dog) }
        }
    }
}

class DogDiffCallback : DiffUtil.ItemCallback<Dog>() {
    override fun areItemsTheSame(oldItem: Dog, newItem: Dog): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Dog, newItem: Dog): Boolean = oldItem == newItem
}
