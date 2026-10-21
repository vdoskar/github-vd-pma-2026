package com.example.objednavkav2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.objednavkav2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.rgMustangs.setOnCheckedChangeListener { _, _ -> updateSelection() }
        binding.cbPhotography.setOnCheckedChangeListener { _, _ -> updateSelection() }
        binding.cbDriftWheels.setOnCheckedChangeListener { _, _ -> updateSelection() }
        binding.btnReserve.setOnClickListener { showSummary() }
        updateSelection()
    }

    private fun selectedMustangName(): String = when (binding.rgMustangs.checkedRadioButtonId) {
        binding.rbV8.id -> binding.rbV8.text.toString()
        binding.rbShelby.id -> binding.rbShelby.text.toString()
        else -> binding.rbEcoboost.text.toString()
    }

    private fun totalPrice(): Int {
        // Ukázkové ceny za jednu hodinu; doplňky se účtují jednou za rezervaci.
        val hourlyRate = when (binding.rgMustangs.checkedRadioButtonId) {
            binding.rbV8.id -> 2500
            binding.rbShelby.id -> 4000
            else -> 1500
        }
        return hourlyRate +
            (if (binding.cbPhotography.isChecked) 500 else 0) +
            (if (binding.cbDriftWheels.isChecked) 1000 else 0)
    }

    private fun updateSelection() {
        val image = when (binding.rgMustangs.checkedRadioButtonId) {
            binding.rbV8.id -> R.drawable.v8
            binding.rbShelby.id -> R.drawable.shelby
            else -> R.drawable.eco
        }
        binding.ivMustang.setImageResource(image)
        binding.ivMustang.contentDescription =
            getString(R.string.mustang_image, selectedMustangName())
        binding.tvTotalPrice.text = getString(R.string.total_price, totalPrice())
    }

    private fun showSummary() {
        val summaryIntent = Intent(this, SummaryActivity::class.java).apply {
            putExtra(EXTRA_MUSTANG_NAME, selectedMustangName())
            putExtra(EXTRA_TOTAL_PRICE, totalPrice())
            putExtra(EXTRA_PHOTOGRAPHY, binding.cbPhotography.isChecked)
            putExtra(EXTRA_DRIFT_WHEELS, binding.cbDriftWheels.isChecked)
        }
        startActivity(summaryIntent)
    }

    companion object {
        const val EXTRA_MUSTANG_NAME = "com.example.objednavkav2.MUSTANG_NAME"
        const val EXTRA_TOTAL_PRICE = "com.example.objednavkav2.TOTAL_PRICE"
        const val EXTRA_PHOTOGRAPHY = "com.example.objednavkav2.PHOTOGRAPHY"
        const val EXTRA_DRIFT_WHEELS = "com.example.objednavkav2.DRIFT_WHEELS"
    }
}
