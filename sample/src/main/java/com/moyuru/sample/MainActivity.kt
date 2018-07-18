package com.moyuru.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.moyuru.cinematiccountdownview.CinematicCountdownView

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val countdownView = findViewById<CinematicCountdownView>(R.id.cinematicCountdownView)
    val button = findViewById<Button>(R.id.button)

    button.setOnClickListener { countdownView.count(10) }
  }
}
