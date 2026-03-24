package com.sportbetlive.sgdsn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.adapter.LeaguePagerAdapter
import com.sportbetlive.sgdsn.data.DataProvider
import com.sportbetlive.sgdsn.databinding.FragmentSportLeagueBinding

class SportLeagueFragment : Fragment() {

    private var _binding: FragmentSportLeagueBinding? = null
    private val binding get() = _binding!!
    private var sportIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSportLeagueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sportIndex = arguments?.getInt("sportIndex", 0) ?: 0
        val sport = DataProvider.getSports()[sportIndex]

        binding.toolbar.title = sport.leagueName
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val tabTitles = listOf("Standings", "Teams")
        binding.viewPager.adapter = LeaguePagerAdapter(this, sportIndex)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.fabSimulate.setOnClickListener {
            val bundle = Bundle().apply { putInt("sportIndex", sportIndex) }
            findNavController().navigate(R.id.action_league_to_matchSim, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
