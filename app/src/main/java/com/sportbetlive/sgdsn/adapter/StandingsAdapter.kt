package com.sportbetlive.sgdsn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportbetlive.sgdsn.data.Standing
import com.sportbetlive.sgdsn.databinding.ItemStandingRowBinding

class StandingsAdapter(
    private val standings: List<Standing>
) : RecyclerView.Adapter<StandingsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemStandingRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(standing: Standing) {
            binding.positionText.text = standing.position.toString()
            binding.teamLogo.setImageResource(standing.team.logoRes)
            binding.teamName.text = standing.team.name
            binding.playedText.text = standing.played.toString()
            binding.winsText.text = standing.wins.toString()
            binding.drawsText.text = standing.draws.toString()
            binding.lossesText.text = standing.losses.toString()
            binding.pointsText.text = standing.points.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStandingRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(standings[position])
    }

    override fun getItemCount() = standings.size
}
