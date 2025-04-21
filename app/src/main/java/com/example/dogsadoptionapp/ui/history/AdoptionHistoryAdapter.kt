package com.example.dogsadoptionapp.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.AdoptionRecord


class AdoptionHistoryAdapter :
    ListAdapter<AdoptionRecord, AdoptionHistoryAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.itemDogImage)
        private val adopter = view.findViewById<TextView>(R.id.itemAdopterName)
        private val date = view.findViewById<TextView>(R.id.itemAdoptionDate)

        fun bind(record: AdoptionRecord) {
            Glide.with(itemView.context).load(record.dogImageUri).into(image)
            adopter.text = itemView.context.getString(
                R.string.adopter_format,
                record.firstName,
                record.lastName
            )
            date.text = record.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_adoption_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<AdoptionRecord>() {
        override fun areItemsTheSame(old: AdoptionRecord, new: AdoptionRecord) = old.id == new.id
        override fun areContentsTheSame(old: AdoptionRecord, new: AdoptionRecord) = old == new
    }
}

