package com.example.a08bdatastore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.a08bdatastore.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Jediná instance DataStore pro celý proces, sdílená oběma fragmenty.
val Context.dataStore by preferencesDataStore(name = "driver_profile")
val DRIVER_NAME_KEY = stringPreferencesKey("DRIVER_NAME")
val SPORT_MODE_KEY = booleanPreferencesKey("SPORT_MODE")

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnSave.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val preferences = requireContext().dataStore.data.first()
                binding.etDriverName.setText(preferences[DRIVER_NAME_KEY] ?: "")
                binding.switchSportMode.isChecked = preferences[SPORT_MODE_KEY] ?: false
                binding.btnSave.isEnabled = true
            } catch (_: IOException) {
                Snackbar.make(binding.root, R.string.load_error, Snackbar.LENGTH_LONG).show()
            }
        }

        binding.btnSave.setOnClickListener {
            val currentName = binding.etDriverName.text.toString()
            val currentSport = binding.switchSportMode.isChecked

            viewLifecycleOwner.lifecycleScope.launch {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSave.isEnabled = false
                binding.btnDashboard.isEnabled = false
                try {
                    // Hlavní vlákno zůstává volné i během simulované práce.
                    delay(3000)
                    requireContext().dataStore.edit { prefs ->
                        prefs[DRIVER_NAME_KEY] = currentName
                        prefs[SPORT_MODE_KEY] = currentSport
                    }

                    Snackbar.make(binding.root, R.string.saved_message, Snackbar.LENGTH_LONG)
                        .setAction(R.string.back_action) {
                            // Stejně jako u učitele: ukázka akce, nikoli skutečné vrácení zápisu.
                            Toast.makeText(requireContext(), R.string.back_action_info, Toast.LENGTH_SHORT).show()
                        }
                        .show()
                } catch (_: IOException) {
                    Snackbar.make(binding.root, R.string.save_error, Snackbar.LENGTH_LONG).show()
                } finally {
                    _binding?.let {
                        it.progressBar.visibility = View.INVISIBLE
                        it.btnSave.isEnabled = true
                        it.btnDashboard.isEnabled = true
                    }
                }
            }
        }

        // Garáž se neukládá s profilem; jde o nezávislý prvek pro porovnání odezvy UI.
        var showShelby = false
        binding.btnChangeMustang.setOnClickListener {
            showShelby = !showShelby
            val label = if (showShelby) R.string.mustang_shelby else R.string.mustang_v8
            binding.ivMustang.setImageResource(if (showShelby) R.drawable.shelby else R.drawable.v8)
            binding.ivMustang.contentDescription = getString(label)
            binding.tvModel.setText(label)
        }
        binding.btnDashboard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DashboardFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
