package com.example.nextvanproto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookmarkAdapter(private val tickets: List<Ticket>) : RecyclerView.Adapter<BookmarkAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyName: TextView = view.findViewById(R.id.tvCompanyName)
        val vanNumber: TextView = view.findViewById(R.id.tvVanNum)
        val departDate: TextView = view.findViewById(R.id.tvDDate)
        val returnDate: TextView = view.findViewById(R.id.tvRDate)
        val fromLocation: TextView = view.findViewById(R.id.tvFrom)
        val toLocation: TextView = view.findViewById(R.id.tvTo)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val referenceNumber: TextView = view.findViewById(R.id.tvReferenceNum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val ticket = tickets[position]

        holder.companyName.text = ticket.company_name
        holder.vanNumber.text = ticket.route_id.toString()
        holder.departDate.text = ticket.depart_date
        holder.returnDate.text = ticket.return_date ?: "N/A"
        holder.fromLocation.text = ticket.from_location
        holder.toLocation.text = ticket.to_location
        holder.price.text = "₱${ticket.total_price}"
        holder.referenceNumber.text = ticket.reference_number
    }

    override fun getItemCount() = tickets.size
}
