package com.example.a04_objednavka

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a04_objednavka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.rgMustangs.setOnCheckedChangeListener { _, _ -> updateOrder() }
        binding.cbPhoto.setOnCheckedChangeListener { _, _ -> updateOrder() }
        binding.cbDriftWheels.setOnCheckedChangeListener { _, _ -> updateOrder() }
        binding.sbDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateOrder()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.btnOrder.setOnClickListener { updateOrder(showSummary = true) }
        updateOrder()
    }

    private fun updateOrder(showSummary: Boolean = false) {
        val mustang = when (binding.rgMustangs.checkedRadioButtonId) {
            R.id.rbMustangV8 -> binding.rbMustangV8
            R.id.rbMustangShelbyGT500 -> binding.rbMustangShelbyGT500
            else -> binding.rbMustangEcoboost
        }
        val hourlyRate = when (mustang.id) {
            R.id.rbMustangV8 -> 2500
            R.id.rbMustangShelbyGT500 -> 4000
            else -> 1500
        }
        val image = when (mustang.id) {
            R.id.rbMustangV8 -> R.drawable.v8
            R.id.rbMustangShelbyGT500 -> R.drawable.shelby
            else -> R.drawable.eco
        }
        val hours = binding.sbDuration.progress
        val photography = binding.cbPhoto.isChecked
        val driftWheels = binding.cbDriftWheels.isChecked
        val total = hourlyRate * hours + (if (photography) 500 else 0) +
            (if (driftWheels) 1000 else 0)

        binding.ivMustang.setImageResource(image)
        binding.ivMustang.contentDescription = getString(R.string.mustang_image, mustang.text)
        binding.tvDuration.text = getString(R.string.rental_duration, hours)
        binding.tvPriceDetails.text = getString(R.string.price_details,
            getString(R.string.price_amount, hourlyRate),
            getString(R.string.price_amount, 500),
            getString(R.string.price_amount, 1000))
        binding.tvTotal.text = getString(R.string.total_price, getString(R.string.price_amount, total))

        if (showSummary) {
            binding.tvOrderSummary.text = getString(R.string.order_summary, mustang.text, hours,
                getString(if (photography) R.string.included else R.string.not_included),
                getString(if (driftWheels) R.string.included else R.string.not_included),
                getString(R.string.price_amount, total))
        }
    }
}