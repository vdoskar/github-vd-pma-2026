package com.example.a08asharedpref

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.a08asharedpref.databinding.FragmentDashboardBinding
import android.content.Context

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
        val sharedPref = requireActivity().getSharedPreferences("DriverProfile", Context.MODE_PRIVATE)
        showProfile(
            sharedPref.getString("DRIVER_NAME", "") ?: "",
            sharedPref.getBoolean("SPORT_MODE", false)
        )
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
