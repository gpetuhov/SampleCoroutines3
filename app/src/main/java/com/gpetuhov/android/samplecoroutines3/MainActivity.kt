package com.gpetuhov.android.samplecoroutines3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

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
        GlobalScope.launch(Dispatchers.Main) {
            val userOne = GlobalScope.async(Dispatchers.IO) { fetchUser() }
            showUserName("Global Scope ${userOne.await()}") // back on UI thread
            Timber.tag("MainActivity").d("Show user name")
        }
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

    private fun fetchUser(): String {
        Thread.sleep(5000)
        return "Bob"
    }
}
