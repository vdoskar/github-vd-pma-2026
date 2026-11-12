package com.example.a08asharedpref

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.a08asharedpref.databinding.FragmentSettingsBinding

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

        val sharedPref = requireActivity().getSharedPreferences("DriverProfile", Context.MODE_PRIVATE)
        binding.etDriverName.setText(sharedPref.getString("DRIVER_NAME", ""))
        binding.switchSportMode.isChecked = sharedPref.getBoolean("SPORT_MODE", false)

        binding.btnSave.setOnClickListener {
            val currentName = binding.etDriverName.text.toString()
            val currentSport = binding.switchSportMode.isChecked

            val editor = sharedPref.edit()
            editor.putString("DRIVER_NAME", currentName)
            editor.putBoolean("SPORT_MODE", currentSport)

            // Záměrně na hlavním vlákně: školní demonstrace zamrznutí celé aplikace.
            Thread.sleep(3000)
            val saved = editor.commit()

            Toast.makeText(
                requireContext(),
                if (saved) R.string.saved_message else R.string.save_error,
                Toast.LENGTH_SHORT
            ).show()
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
