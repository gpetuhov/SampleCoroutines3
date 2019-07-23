package com.gpetuhov.android.samplecoroutines3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

// For coroutines to live only during MainActivity lifecycle we need to implement CoroutineScope
class MainActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        private const val TAG = "MainActivity"
    }

    // We have to define CoroutineContext for MainActivity.
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + handler // We can use + sign, because CoroutineContext overrides plus operator

    // This job represents running coroutine.
    private lateinit var job: Job

    // We can create our own exception handler like this
    private val handler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).d("$exception handled!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Don't forget to create Job in onCreate()
        job = Job()

        load_global_button.setOnClickListener { loadUserWithGlobalScope() }
        load_local_button.setOnClickListener { loadUserWithActivityScope() }
        reset_button.setOnClickListener { resetUser() }
    }

    override fun onDestroy() {
        // Don't forget to cancel Job in onDestroy()
        job.cancel()
        super.onDestroy()
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

        // Despatchers.Main tells coroutine to run on main thread, because here we update UI.
        // We also provide our own exception handler.

        // Notice that launch and async are called "coroutine builders".
        // Every function that is declared as extension on CoroutineScope returns immediately,
        // but performs its actions concurrently with the rest of the program.
        GlobalScope.launch(Dispatchers.Main + handler) {
            Timber.tag(TAG).d("Start load user")

            // Here we use Dispatchers.IO, so fetchUser is executed in the thread pool,
            // specifically designed for blocking IO operations
            // (for computational tasks Dispatchers.Default should be used).
            // This coroutine will not start immediately, because async is used.
            // The coroutine will start when await is called.
            // We use async here instead of launch, because fetchUser() returns some result.
            val user = GlobalScope.async(Dispatchers.IO) { fetchUser() }

            // Here we start fetchUser() on background thread. Results are returned to main thread.
            showUserName("Global Scope ${user.await()}")

            // We could do it another way by using withContext instead of async like this:
//            val user = withContext(Dispatchers.IO) { fetchUser() }
//            showUserName("Global Scope $user")

            // This log statement will show even if MainActivity is destroyed
            Timber.tag(TAG).d("Show user name")
        }

        // Notice that here exceptions in async are also handled, because it is a child job of launch,
        // which runs with MainActivity's CoroutineScope, for which we provided our own exception handler.
        // If we want to handle exceptions in async alone, we should use try-catch block like this:
//        val deferredUser = GlobalScope.async { fetchUser() }
//        try {
//            val user = deferredUser.await()
//        } catch (exception: Exception) {
//            Timber.tag(TAG).d("$exception handled!")
//        }
    }

    private fun loadUserWithActivityScope() {
        // This coroutine uses MainActivity's scope.
        // So if MainActivity is destroyed, the coroutine is canceled.
        // Notice that launch and async are using "this" (MainActivity) as CoroutineScope.
        // Job for the coroutine is saved into job property,
        // because we included it into CoroutineContext of MainActivity.
        // Coroutine will run on main thread,
        // because we defined Dispatchers.Main in CoroutineContext of MainActivity.
        launch {
            Timber.tag(TAG).d("Start load user")

            // fetchUser() will run on background thread, when await() is called
            val user = async(Dispatchers.IO) { fetchUser() }

            showUserName("Activity Scope ${user.await()}")

            // This log statement will show only if MainActivity is NOT destroyed,
            // while coroutine is running.
            Timber.tag(TAG).d("Show user name")
        }
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
