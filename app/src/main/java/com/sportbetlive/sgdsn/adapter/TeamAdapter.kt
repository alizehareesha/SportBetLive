package com.sportbetlive.sgdsn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportbetlive.sgdsn.data.Team
import com.sportbetlive.sgdsn.databinding.ItemTeamBinding

class TeamAdapter(
    private val teams: List<Team>,
    private val onItemClick: (Team) -> Unit
) : RecyclerView.Adapter<TeamAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(team: Team) {
            binding.teamLogo.setImageResource(team.logoRes)
            binding.teamName.text = team.name
            binding.teamArena.text = team.arena
            binding.root.setOnClickListener { onItemClick(team) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTeamBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount() = teams.size
}
