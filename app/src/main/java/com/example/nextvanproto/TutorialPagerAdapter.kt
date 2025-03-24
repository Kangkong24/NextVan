package com.example.nextvanproto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class TutorialPagerAdapter(private val images: List<Int>) : RecyclerView.Adapter<TutorialPagerAdapter.TutorialViewHolder>() {

    inner class TutorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgTutorial: ImageView = view.findViewById(R.id.imgTutorial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutorial, parent, false)
        return TutorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        holder.imgTutorial.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size
}
