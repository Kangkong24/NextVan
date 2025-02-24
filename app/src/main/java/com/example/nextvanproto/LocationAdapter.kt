package com.example.nextvanproto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(
    private var locationList: List<Location>,
    private val onItemClick: (String) -> Unit // Click listener
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locationList[position], onItemClick)
    }

    override fun getItemCount(): Int = locationList.size

    fun updateList(newList: List<Location>) {
        locationList = newList
        notifyDataSetChanged()
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)

        fun bind(location: Location, onItemClick: (String) -> Unit) {
            tvLocation.text = location.name
            itemView.setOnClickListener { onItemClick(location.name) }
        }
    }
}

