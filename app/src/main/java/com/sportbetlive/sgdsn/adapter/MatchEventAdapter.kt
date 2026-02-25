package com.sportbetlive.sgdsn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportbetlive.sgdsn.data.MatchEvent
import com.sportbetlive.sgdsn.databinding.ItemMatchEventBinding

class MatchEventAdapter(
    private val events: List<MatchEvent>
) : RecyclerView.Adapter<MatchEventAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMatchEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: MatchEvent) {
            binding.minuteText.text = "${event.minute}'"
            binding.eventDescription.text = event.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size
}
