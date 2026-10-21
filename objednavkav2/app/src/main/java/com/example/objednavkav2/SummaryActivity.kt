package com.example.objednavkav2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.objednavkav2.databinding.ActivitySummaryBinding

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val mustang = intent.getStringExtra(MainActivity.EXTRA_MUSTANG_NAME)
            ?: getString(R.string.unknown_mustang)
        val photography = intent.getBooleanExtra(MainActivity.EXTRA_PHOTOGRAPHY, false)
        val driftWheels = intent.getBooleanExtra(MainActivity.EXTRA_DRIFT_WHEELS, false)
        val price = intent.getIntExtra(MainActivity.EXTRA_TOTAL_PRICE, 0)

        val selectedExtras = buildList {
            if (photography) add(getString(R.string.option_photography))
            if (driftWheels) add(getString(R.string.option_drift_wheels))
        }

        binding.tvSelectedBike.text = getString(R.string.selected_mustang, mustang)
        binding.tvExtras.text = getString(
            R.string.selected_extras,
            if (selectedExtras.isEmpty()) getString(R.string.no_extras)
            else selectedExtras.joinToString("\n") { "• $it" }
        )
        binding.tvFinalPrice.text = getString(R.string.total_price, price)
        binding.btnBack.setOnClickListener { finish() }
    }
}
