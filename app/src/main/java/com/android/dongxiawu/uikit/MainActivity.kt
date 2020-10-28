package com.android.dongxiawu.uikit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.icon).setOnClickListener {
            it.isSelected = !it.isSelected
        }
        findViewById<View>(R.id.icon2).setOnClickListener {
            it.isSelected = !it.isSelected
        }
        findViewById<View>(R.id.icon3).setOnClickListener {
            it.isEnabled = !it.isEnabled
        }
        findViewById<View>(R.id.icon4).setOnClickListener {
            it.isSelected = !it.isSelected
        }
        findViewById<View>(R.id.icon5).setOnClickListener {
            it.isSelected = !it.isSelected
        }
        findViewById<View>(R.id.icon6).setOnClickListener {
            it.isSelected = !it.isSelected
        }
        findViewById<View>(R.id.icon7).setOnClickListener {
            it.isActivated = !it.isActivated
        }
        findViewById<View>(R.id.icon8).setOnClickListener {
            it.onWindowFocusChanged(!it.hasWindowFocus())
        }
        findViewById<View>(R.id.icon9).setOnClickListener {
            it.isSelected = !it.isSelected
        }
//        findViewById<View>(R.id.icon10).setOnClickListener {
//            it.isPressed = !it.isPressed
//        }
    }
}