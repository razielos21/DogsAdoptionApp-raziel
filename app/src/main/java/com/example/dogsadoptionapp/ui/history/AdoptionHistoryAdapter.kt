package com.example.dogsadoptionapp.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.data.model.AdoptionRecord
import com.example.dogsadoptionapp.databinding.ItemAdoptionHistoryBinding

class AdoptionHistoryAdapter :
    ListAdapter<AdoptionRecord, AdoptionHistoryAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(private val binding: ItemAdoptionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: AdoptionRecord) {
            Glide.with(itemView.context).load(record.dogImageUri).into(binding.itemDogImage)
            binding.itemAdopterName.text = itemView.context.getString(
                com.example.dogsadoptionapp.R.string.adopter_format,
                record.firstName,
                record.lastName
            )
            binding.itemAdoptionDate.text = record.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdoptionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<AdoptionRecord>() {
        override fun areItemsTheSame(old: AdoptionRecord, new: AdoptionRecord) = old.id == new.id
        override fun areContentsTheSame(old: AdoptionRecord, new: AdoptionRecord) = old == new
    }
}