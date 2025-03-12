package com.example.nextvanproto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nextvanproto.databinding.SeatItemBinding

class SeatListAdapter(
    private val seatList: List<Seat>,
    private val context: Context,
    private val selectedSeat: SelectedSeat
) : RecyclerView.Adapter<SeatListAdapter.SeatViewHolder>() {

    private val selectedSeatName = mutableListOf<String>()

    // Add a flag to indicate if we should add an empty space at the beginning
    private val hasEmptyFirstSpace = true

    class SeatViewHolder(val binding: SeatItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        return SeatViewHolder(
            SeatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        // If it's the first position and we want an empty space
        if (position == 0 && hasEmptyFirstSpace) {
            // Make the view invisible/empty
            holder.binding.seat.visibility = View.INVISIBLE
            // Disable click listener
            holder.binding.seat.setOnClickListener(null)
            return
        }

        // Get the correct seat from the list
        // If we have an empty first space, offset by 1
        val seatPosition = if (hasEmptyFirstSpace) position - 1 else position

        // Check if we're trying to access a valid seat (important when we add an empty first slot)
        if (seatPosition >= seatList.size || seatPosition < 0) {
            holder.binding.seat.visibility = View.INVISIBLE
            holder.binding.seat.setOnClickListener(null)
            return
        }

        val seat = seatList[seatPosition]
        holder.binding.seat.visibility = View.VISIBLE
        holder.binding.seat.text = seat.name

        when (seat.status) {
            Seat.SeatStatus.AVAILABLE -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_available)
                holder.binding.seat.setTextColor(context.getColor(R.color.white))
            }
            Seat.SeatStatus.SELECTED -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_selected)
                holder.binding.seat.setTextColor(context.getColor(R.color.black))
            }
            Seat.SeatStatus.UNAVAILABLE -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_unavailable)
                holder.binding.seat.setTextColor(context.getColor(R.color.darker_grey))
            }
        }

        holder.binding.seat.setOnClickListener {
            when (seat.status) {
                Seat.SeatStatus.AVAILABLE -> {
                    seat.status = Seat.SeatStatus.SELECTED
                    selectedSeatName.add(seat.name)
                }
                Seat.SeatStatus.SELECTED -> {
                    seat.status = Seat.SeatStatus.AVAILABLE
                    selectedSeatName.remove(seat.name)
                }
                else -> {}
            }
            notifyItemChanged(position)
            selectedSeat.Return(selectedSeatName.joinToString(", "), selectedSeatName.size)
        }
    }

    // Increase the item count by 1 if we have an empty first space
    override fun getItemCount(): Int = if (hasEmptyFirstSpace) seatList.size + 1 else seatList.size

    interface SelectedSeat {
        fun Return(selectedNames: String, num: Int)
    }
}