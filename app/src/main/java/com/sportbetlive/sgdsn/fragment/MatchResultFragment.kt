package com.sportbetlive.sgdsn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.adapter.MatchEventAdapter
import com.sportbetlive.sgdsn.data.DataProvider
import com.sportbetlive.sgdsn.data.MatchResult
import com.sportbetlive.sgdsn.data.SportType
import com.sportbetlive.sgdsn.databinding.FragmentMatchResultBinding
import com.sportbetlive.sgdsn.util.DataVault
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MatchResultFragment : Fragment() {

    private var _binding: FragmentMatchResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sportIndex = arguments?.getInt("sportIndex", 0) ?: 0
        val homeTeamId = arguments?.getInt("homeTeamId", 0) ?: 0
        val awayTeamId = arguments?.getInt("awayTeamId", 0) ?: 0

        val sportType = SportType.entries[sportIndex]
        val teams = DataProvider.getTeams(sportType)
        val homeTeam = teams.find { it.id == homeTeamId } ?: teams[0]
        val awayTeam = teams.find { it.id == awayTeamId } ?: teams[1]

        val result = DataProvider.simulateMatch(homeTeam, awayTeam, sportType)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.homeLogo.setImageResource(result.homeTeam.logoRes)
        binding.homeNameText.text = result.homeTeam.name
        binding.awayLogo.setImageResource(result.awayTeam.logoRes)
        binding.awayNameText.text = result.awayTeam.name
        binding.scoreText.text = "0 - 0"
        binding.periodLabel.text = getString(R.string.simulating)

        runSimulation(result, sportType, homeTeamId, awayTeamId)
    }

    private fun runSimulation(result: MatchResult, sportType: SportType, homeId: Int, awayId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            var totalHome = 0
            var totalAway = 0

            delay(800)

            for ((idx, period) in result.periods.withIndex()) {
                binding.periodLabel.text = period.periodName

                delay(1200)

                totalHome += period.homeScore
                totalAway += period.awayScore
                binding.scoreText.text = "$totalHome - $totalAway"

                addPeriodRow(period.periodName, period.homeScore, period.awayScore)
                binding.periodScoresContainer.visibility = View.VISIBLE

                if (idx < result.periods.size - 1) {
                    delay(1000)
                }
            }

            delay(600)

            binding.periodLabel.text = getString(R.string.final_score)

            binding.eventsTitle.visibility = View.VISIBLE
            binding.eventsRecycler.visibility = View.VISIBLE
            binding.eventsRecycler.layoutManager = LinearLayoutManager(requireContext())
            binding.eventsRecycler.adapter = MatchEventAdapter(result.events)

            val vault = DataVault(requireContext())
            vault.updateStandingsAfterMatch(
                sportType, homeId, awayId, result.homeScore, result.awayScore
            )
            vault.saveMatchRecord(
                sportType, homeId, awayId, result.homeScore, result.awayScore
            )
        }
    }

    private fun addPeriodRow(name: String, homeScore: Int, awayScore: Int) {
        val row = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_period_score, binding.periodScoresContainer, false)
        row.findViewById<TextView>(R.id.periodName).text = name
        row.findViewById<TextView>(R.id.periodHomeScore).text = homeScore.toString()
        row.findViewById<TextView>(R.id.periodAwayScore).text = awayScore.toString()
        binding.periodScoresContainer.addView(row)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
