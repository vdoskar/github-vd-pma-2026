package com.example.a01_hod_kostkou

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Odsazení obsahu od systémových lišt
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.llMain)
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // Propojíme prvky obrazovky s logikou hodu
        val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")
        val diceView = findViewById<TextView>(R.id.tvDice)
        val rollButton = findViewById<Button>(R.id.btnRoll)

        rollButton.setOnClickListener {
            lifecycleScope.launch {
                rollButton.isEnabled = false
                repeat(10) {
                    diceView.text = diceSymbols.random()
                    delay(250)
                }

                val diceValue = (1..6).random()
                diceView.text = diceSymbols[diceValue - 1]
                rollButton.isEnabled = true
            }
        }
    }
}
