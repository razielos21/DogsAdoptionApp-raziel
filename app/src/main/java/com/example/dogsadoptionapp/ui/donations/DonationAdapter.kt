package com.example.dogsadoptionapp.ui.donations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.Donation

class DonationAdapter(
    private val onItemClick: (Donation) -> Unit
) : RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

    private var donations: List<Donation> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        holder.bind(donations[position])
    }

    override fun getItemCount(): Int = donations.size

    fun submitList(list: List<Donation>) {
        donations = list
        notifyDataSetChanged()
    }

    inner class DonationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val donorName: TextView = view.findViewById(R.id.textDonorName)
        private val category: TextView = view.findViewById(R.id.textCategory)
        private val description: TextView = view.findViewById(R.id.textDescription)
        private val date: TextView = view.findViewById(R.id.textDate)

        fun bind(donation: Donation) {
            donorName.text = donation.donorName
            category.text = itemView.context.getString(
                R.string.category_format, donation.category.name
            )
            description.text = donation.description
            date.text = donation.date

            itemView.setOnClickListener {
                onItemClick(donation)
            }
        }
    }
}
