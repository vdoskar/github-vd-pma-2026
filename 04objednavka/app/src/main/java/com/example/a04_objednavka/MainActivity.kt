package com.example.a04_objednavka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a04_objednavka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnOrder.setOnClickListener {
            val mustang = when(binding.rgMustangs.checkedRadioButtonId) {
                binding.rbMustangEcoboost.id -> binding.rbMustangEcoboost
                binding.rbMustangV8.id -> binding.rbMustangV8
                binding.rbMustangShelbyGT500.id -> binding.rbMustangShelbyGT500
                else -> binding.rbMustangEcoboost
            }

            val photography = binding.cbPhoto.isChecked
            val driftWheels = binding.cbDriftWheels.isChecked

            val orderText = "Objednávky:\n" + mustang.text + "\n" + (if (photography) "- S foto\n" else "") + (if (driftWheels) "- S driftovacími pneu\n" else "")

            binding.tvOrderSummary.text = orderText
        }

        binding.rbMustangEcoboost.setOnClickListener {
            binding.ivMustang.setImageResource(R.drawable.eco)
        }
        binding.rbMustangV8.setOnClickListener {
            binding.ivMustang.setImageResource(R.drawable.v8)
        }
        binding.rbMustangShelbyGT500.setOnClickListener {
            binding.ivMustang.setImageResource(R.drawable.shelby)
        }
    }
}
