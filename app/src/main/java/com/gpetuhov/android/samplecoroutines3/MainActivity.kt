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

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        load_global_button.setOnClickListener { loadUserWithGlobalScope() }
        load_local_button.setOnClickListener { loadUserWithActivityScope() }
        reset_button.setOnClickListener { resetUser() }
    }

    // === Private methods ===

    private fun loadUserWithGlobalScope() {
        // Here we use GlobalScope.
        // This means that the coroutine will continue running (until completion or exception)
        // as long as the application is running.
        // If MainActivity is destroyed (for example, on screen rotation),
        // coroutine will NOT be canceled, but results will not show on screen,
        // because they will be delivered to the old activity, which is not on screen any more.
        // This could lead to memory leaks.

        // Despatchers.Main tells coroutine to run on main thread, because here we update UI
        GlobalScope.launch(Dispatchers.Main) {
            Timber.tag(TAG).d("Start load user")

            // Here we use Dispatchers.IO, so fetchUser is executed in the thread pool,
            // specifically designed for blocking IO operations.
            // This coroutine will not start immediately, because async is used.
            // The coroutine will start when await is called.
            val user = GlobalScope.async(Dispatchers.IO) { fetchUser() }

            // Here we start fetchUser() on background thread. Results are returned to main thread.
            showUserName("Global Scope ${user.await()}")

            // This log statement will show even if MainActivity is destroyed
            Timber.tag(TAG).d("Show user name")
        }
    }

    private fun loadUserWithActivityScope() {
        // TODO
        showUserName("Activity Scope Bob")
    }

    private fun resetUser() {
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
