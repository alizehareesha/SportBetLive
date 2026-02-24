package com.sportbetlive.sgdsn.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.activity.PolicyActivity
import com.sportbetlive.sgdsn.adapter.SportCardAdapter
import com.sportbetlive.sgdsn.data.DataProvider
import com.sportbetlive.sgdsn.databinding.FragmentHomeBinding
import com.sportbetlive.sgdsn.util.DataVault

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sports = DataProvider.getSports()
        val adapter = SportCardAdapter(sports) { index ->
            val bundle = Bundle().apply { putInt("sportIndex", index) }
            findNavController().navigate(R.id.action_home_to_sportLeague, bundle)
        }

        binding.sportsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.sportsRecycler.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                findNavController().navigate(R.id.action_home_to_settings)
                true
            } else false
        }

        binding.policyButton.setOnClickListener {
            val vault = DataVault(requireContext())
            val link = vault.getPolicyLink() ?: ""
            if (link.isNotEmpty()) {
                val intent = Intent(requireContext(), PolicyActivity::class.java)
                intent.putExtra(PolicyActivity.EXTRA_POLICY_LINK, link)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
