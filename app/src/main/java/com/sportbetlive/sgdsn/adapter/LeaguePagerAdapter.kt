package com.sportbetlive.sgdsn.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sportbetlive.sgdsn.fragment.StandingsFragment
import com.sportbetlive.sgdsn.fragment.TeamListFragment

class LeaguePagerAdapter(
    fragment: Fragment,
    private val sportIndex: Int
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StandingsFragment.newInstance(sportIndex)
            1 -> TeamListFragment.newInstance(sportIndex)
            else -> StandingsFragment.newInstance(sportIndex)
        }
    }
}
