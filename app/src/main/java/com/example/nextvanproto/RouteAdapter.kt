package com.example.nextvanproto

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RouteAdapter(
    private val routes: List<Route>,
    private val onItemClick: (Route) -> Unit // Click listener
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    class RouteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyLogo: ImageView = view.findViewById(R.id.imageView5)
        val arriveTime: TextView = view.findViewById(R.id.textView14)
        val vanNum: TextView = view.findViewById(R.id.tvVanNum)
        val fromLocation: TextView = view.findViewById(R.id.textView15)
        val toLocation: TextView = view.findViewById(R.id.textView16)
        val price: TextView = view.findViewById(R.id.textView17)

        fun bind(route: Route, onItemClick: (Route) -> Unit) {
            Glide.with(itemView.context)
                .load(route.company_logo)
                .into(companyLogo)

            vanNum.text = route.id.toString()
            arriveTime.text = route.arrive_time
            fromLocation.text = route.from_location
            toLocation.text = route.to_location
            price.text = "₱${route.price}"

            itemView.setOnClickListener { onItemClick(route) } // Set click listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_ticket, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routes[position], onItemClick)
    }

    override fun getItemCount() = routes.size
}
