package com.sportbetlive.sgdsn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.adapter.TeamAdapter
import com.sportbetlive.sgdsn.data.DataProvider
import com.sportbetlive.sgdsn.data.SportType
import com.sportbetlive.sgdsn.databinding.FragmentTeamListBinding

class TeamListFragment : Fragment() {

    private var _binding: FragmentTeamListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sportIndex = arguments?.getInt(ARG_SPORT_INDEX, 0) ?: 0
        val sportType = SportType.entries[sportIndex]
        val teams = DataProvider.getTeams(sportType)

        binding.teamsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.teamsRecycler.adapter = TeamAdapter(teams) { team ->
            val bundle = Bundle().apply {
                putInt("teamId", team.id)
                putInt("sportIndex", sportIndex)
            }
            parentFragment?.parentFragment?.let { host ->
                host.findNavController().navigate(R.id.action_league_to_teamDetail, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SPORT_INDEX = "sport_index"
        fun newInstance(sportIndex: Int): TeamListFragment {
            return TeamListFragment().apply {
                arguments = Bundle().apply { putInt(ARG_SPORT_INDEX, sportIndex) }
            }
        }
    }
}
