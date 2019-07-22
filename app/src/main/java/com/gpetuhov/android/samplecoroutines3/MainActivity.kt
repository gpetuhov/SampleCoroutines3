package com.gpetuhov.android.samplecoroutines3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        load_global_button.setOnClickListener { loadUserWithGlobalScope() }
        load_local_button.setOnClickListener { loadUserWithActivityScope() }
        reset_button.setOnClickListener { resetUser() }
    }

    // === Private methods ===

    private fun loadUserWithGlobalScope() {
        // TODO
        showUserName("Global Scope Bob")
    }

    private fun loadUserWithActivityScope() {
        // TODO
        showUserName("Activity Scope Bob")
    }

    private fun resetUser() {
        // TODO
        showUserName("Hello World!")
    }

    private fun showUserName(name: String) {
        user_name.text = name
    }
}
