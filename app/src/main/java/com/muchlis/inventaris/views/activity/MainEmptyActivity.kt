package com.muchlis.inventaris.views.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.views.activity.dashboard.DashboardActivity
import com.muchlis.inventaris.views.activity.login.LoginActivity

class MainEmptyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*Jika sharedpreference untuk token kosong maka akan diarahkan ke login activity
        * Jika isi akan diarahkan ke dashboard activity*/
        val activityIntent: Intent = if (App.prefs.authTokenSave.isEmpty()) {
            Intent(this, LoginActivity::class.java)
        } else {
            Intent(this, DashboardActivity::class.java)
        }

        startActivity(activityIntent)
        finish()

    }
}