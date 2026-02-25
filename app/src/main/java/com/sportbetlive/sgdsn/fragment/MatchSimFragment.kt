package com.sportbetlive.sgdsn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.data.DataProvider
import com.sportbetlive.sgdsn.data.SportType
import com.sportbetlive.sgdsn.data.Team
import com.sportbetlive.sgdsn.databinding.FragmentMatchSimBinding

class MatchSimFragment : Fragment() {

    private var _binding: FragmentMatchSimBinding? = null
    private val binding get() = _binding!!
    private var sportIndex = 0
    private lateinit var teams: List<Team>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchSimBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sportIndex = arguments?.getInt("sportIndex", 0) ?: 0
        val sportType = SportType.entries[sportIndex]
        teams = DataProvider.getTeams(sportType)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val teamNames = teams.map { it.name }
        val spinnerAdapter = object : ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, teamNames
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as TextView).setTextColor(resources.getColor(R.color.white, null))
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                (v as TextView).setTextColor(resources.getColor(R.color.white, null))
                v.setBackgroundColor(resources.getColor(R.color.card_bg, null))
                return v
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.homeSpinner.adapter = spinnerAdapter
        binding.awaySpinner.adapter = spinnerAdapter
        if (teams.size > 1) binding.awaySpinner.setSelection(1)

        binding.simulateButton.setOnClickListener {
            val homePos = binding.homeSpinner.selectedItemPosition
            val awayPos = binding.awaySpinner.selectedItemPosition
            val bundle = Bundle().apply {
                putInt("sportIndex", sportIndex)
                putInt("homeTeamId", teams[homePos].id)
                putInt("awayTeamId", teams[awayPos].id)
            }
            findNavController().navigate(R.id.action_matchSim_to_matchResult, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
