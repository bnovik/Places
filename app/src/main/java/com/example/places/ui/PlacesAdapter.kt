package com.example.places.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.places.R
import com.example.places.domain.models.Place

class PlacesAdapter() : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    private val data: MutableList<Place> = mutableListOf()

    fun updateList(places: List<Place>) {
        data.clear()
        data.addAll(places)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder =
        PlaceViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        )


    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(place: Place) {
            itemView.findViewById<TextView>(R.id.textViewName).text = place.name
            itemView.findViewById<TextView>(R.id.textViewAddress).text = place.address
        }
    }


}