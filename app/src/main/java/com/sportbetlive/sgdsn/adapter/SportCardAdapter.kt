package com.sportbetlive.sgdsn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportbetlive.sgdsn.data.Sport
import com.sportbetlive.sgdsn.databinding.ItemSportCardBinding

class SportCardAdapter(
    private val sports: List<Sport>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SportCardAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSportCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sport: Sport, position: Int) {
            binding.sportIcon.setImageResource(sport.iconRes)
            binding.sportName.text = sport.name
            binding.leagueName.text = sport.leagueName
            binding.root.setOnClickListener { onItemClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSportCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sports[position], position)
    }

    override fun getItemCount() = sports.size
}
