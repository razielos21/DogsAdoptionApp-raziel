package com.example.dogsadoptionapp.ui.stray

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogsadoptionapp.R
import com.example.dogsadoptionapp.data.model.StrayReport
import java.text.SimpleDateFormat
import java.util.*

class StrayReportAdapter(
    private val reports: List<StrayReport>,
    private val onItemClick: (StrayReport) -> Unit
) : RecyclerView.Adapter<StrayReportAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.reportImage)
        private val location = view.findViewById<TextView>(R.id.reportLocation)
        private val dateTime = view.findViewById<TextView>(R.id.reportDateTime)

        fun bind(report: StrayReport) {
            Glide.with(itemView.context)
                .load(report.imageUri)
                .placeholder(R.drawable.barkbuddy_icon_foreground)
                .into(image)

            location.text = report.address

            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            dateTime.text = formatter.format(Date(report.timestamp))

            itemView.setOnClickListener {
                onItemClick(report)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stray_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    override fun getItemCount(): Int = reports.size
}
