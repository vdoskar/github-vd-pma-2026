package com.example.a08bdatastore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.a08bdatastore.databinding.FragmentDashboardBinding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prefs = requireContext().dataStore.data.first()
                showProfile(prefs[DRIVER_NAME_KEY] ?: "", prefs[SPORT_MODE_KEY] ?: false)
            } catch (_: IOException) {
                Snackbar.make(binding.root, R.string.load_error, Snackbar.LENGTH_LONG).show()
            }
        }
        binding.btnSettings.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showProfile(name: String, sportMode: Boolean) {
        binding.tvDriverName.text = getString(
            R.string.driver_summary, name.ifBlank { getString(R.string.no_driver) }
        )
        binding.tvSportMode.setText(if (sportMode) R.string.sport_enabled else R.string.sport_disabled)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
