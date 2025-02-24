package com.example.nextvanproto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ExploreRouteAdapter(private val routes: List<Route>) :
    RecyclerView.Adapter<ExploreRouteAdapter.RouteViewHolder>() { // Fixed the ViewHolder reference

    inner class RouteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyLogo: ImageView = view.findViewById(R.id.imageView5)
        val arriveTime: TextView = view.findViewById(R.id.textView14)
        val fromLocation: TextView = view.findViewById(R.id.textView15)
        val toLocation: TextView = view.findViewById(R.id.textView16)
        val price: TextView = view.findViewById(R.id.textView17)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_ticket, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]

        Glide.with(holder.itemView.context)
            .load(route.company_logo) // Ensure this matches your data class field name
            .into(holder.companyLogo)

        holder.arriveTime.text = route.arrive_time
        holder.fromLocation.text = route.from_location
        holder.toLocation.text = route.to_location
        holder.price.text = "20% ₱${route.price - 20}"
    }

    override fun getItemCount() = routes.size
}