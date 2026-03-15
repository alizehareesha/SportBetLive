package com.sportbetlive.sgdsn.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.activity.PhoneVerificationActivity
import com.sportbetlive.sgdsn.databinding.FragmentSettingsBinding
import com.sportbetlive.sgdsn.util.DataVault

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.versionText.visibility = View.GONE

        binding.resetButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.reset_confirm_title)
                .setMessage(R.string.reset_confirm_message)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    DataVault(requireContext()).clearSimulationData()
                    clearVerificationData()
                    Toast.makeText(requireContext(), R.string.data_reset_success, Toast.LENGTH_SHORT).show()
                    goToVerificationScreen()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun clearVerificationData() {
        val prefs = requireContext().getSharedPreferences("phone_verification_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    private fun goToVerificationScreen() {
        val intent = Intent(requireContext(), PhoneVerificationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
