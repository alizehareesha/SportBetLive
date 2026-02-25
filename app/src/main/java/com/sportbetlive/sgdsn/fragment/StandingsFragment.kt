package com.sportbetlive.sgdsn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportbetlive.sgdsn.adapter.StandingsAdapter
import com.sportbetlive.sgdsn.data.DataProvider
import com.sportbetlive.sgdsn.data.SportType
import com.sportbetlive.sgdsn.databinding.FragmentStandingsBinding
import com.sportbetlive.sgdsn.util.DataVault

class StandingsFragment : Fragment() {

    private var _binding: FragmentStandingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sportIndex = arguments?.getInt(ARG_SPORT_INDEX, 0) ?: 0
        val sportType = SportType.entries[sportIndex]
        val vault = DataVault(requireContext())
        val standings = vault.getStandings(sportType) ?: DataProvider.emptyStandings(sportType)
        binding.standingsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.standingsRecycler.adapter = StandingsAdapter(standings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SPORT_INDEX = "sport_index"
        fun newInstance(sportIndex: Int): StandingsFragment {
            return StandingsFragment().apply {
                arguments = Bundle().apply { putInt(ARG_SPORT_INDEX, sportIndex) }
            }
        }
    }
}
