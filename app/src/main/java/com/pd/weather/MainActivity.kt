package com.pd.weather

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var getForecastButton = findViewById<Button>(R.id.getForecastButton)
        getForecastButton.setOnClickListener {
            var foreCastIntent = Intent(this, ForecastActivity::class.java)
            foreCastIntent.putExtra("cityName", cityNameEditText.text.toString())
            startActivity(foreCastIntent)
        }
    }
}
