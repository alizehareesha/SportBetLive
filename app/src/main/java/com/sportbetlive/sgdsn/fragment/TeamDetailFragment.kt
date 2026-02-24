package com.sportbetlive.sgdsn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.data.DataProvider
import com.sportbetlive.sgdsn.data.MatchRecord
import com.sportbetlive.sgdsn.data.SportType
import com.sportbetlive.sgdsn.databinding.FragmentTeamDetailBinding
import com.sportbetlive.sgdsn.util.DataVault

class TeamDetailFragment : Fragment() {

    private var _binding: FragmentTeamDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val teamId = arguments?.getInt("teamId", 0) ?: 0
        val sportIndex = arguments?.getInt("sportIndex", 0) ?: 0
        val sportType = SportType.entries[sportIndex]
        val team = DataProvider.getTeams(sportType).find { it.id == teamId } ?: return

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.teamLogo.setImageResource(team.logoRes)
        binding.teamName.text = team.name
        binding.teamShortName.text = team.shortName
        binding.foundedText.text = team.founded.toString()
        binding.arenaText.text = team.arena

        val vault = DataVault(requireContext())
        val history = vault.getMatchHistory(sportType, teamId)

        binding.recentTitle.visibility = View.VISIBLE

        if (history.isEmpty()) {
            binding.noMatchesText.visibility = View.VISIBLE
        } else {
            binding.matchHistoryContainer.visibility = View.VISIBLE
            for (record in history) {
                addMatchRow(record)
            }
        }
    }

    private fun addMatchRow(record: MatchRecord) {
        val row = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_match_record, binding.matchHistoryContainer, false)
        row.findViewById<ImageView>(R.id.homeLogo).setImageResource(record.homeTeam.logoRes)
        row.findViewById<TextView>(R.id.homeName).text = record.homeTeam.shortName
        row.findViewById<TextView>(R.id.scoreText).text = "${record.homeScore} - ${record.awayScore}"
        row.findViewById<TextView>(R.id.awayName).text = record.awayTeam.shortName
        row.findViewById<ImageView>(R.id.awayLogo).setImageResource(record.awayTeam.logoRes)
        binding.matchHistoryContainer.addView(row)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
