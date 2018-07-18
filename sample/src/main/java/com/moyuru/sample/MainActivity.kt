package com.moyuru.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.moyuru.cinematiccountdownview.CinematicCountdownView
import kotlin.reflect.KProperty

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val countdownView = findViewById<CinematicCountdownView>(R.id.cinematicCountdownView)
    val button = findViewById<Button>(R.id.button)

    button.setOnClickListener { countdownView.count(10) }
  }
}
