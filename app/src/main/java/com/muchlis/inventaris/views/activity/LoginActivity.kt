package com.muchlis.inventaris.views.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.LoginRequest
import com.muchlis.inventaris.databinding.ActivityLoginBinding
import com.muchlis.inventaris.utils.invisible
import com.muchlis.inventaris.utils.visible
import com.muchlis.inventaris.view_model.LoginViewModel
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity(){
    private lateinit var bd: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityLoginBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        hideNotificationBar()
        observeViewModel()

        bd.btnLoginLogin.setOnClickListener {
            val username = bd.edtUsernameLogin.editText?.text?.toString()?.trim() ?: ""
            val password = bd.edtPasswordLogin.editText?.text?.toString() ?: ""

            if (username.isEmpty() || password.isEmpty()) {
                Toasty.info(
                    this,
                    "Username dan Password tidak boleh kosong!",
                    Toasty.LENGTH_SHORT
                ).show()
            } else {
                val loginInput = LoginRequest(username = username, password = password)
                viewModel.doLogin(loginInput)
            }


        }
    }

    private fun observeViewModel() {

        viewModel.run {
            isLoginSuccess.observe(this@LoginActivity, Observer { navigateToDashboardActivity(it) })
            isLoading.observe(this@LoginActivity, Observer { showLoading(it) })
            isError.observe(this@LoginActivity, Observer { showErrorToast(it) })
        }
    }

    private fun hideNotificationBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }

    private fun navigateToDashboardActivity(ok: Boolean) {
        if (ok) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bd.pbLogin.visible()
            bd.btnLoginLogin.invisible()
        } else {
            bd.pbLogin.invisible()
            bd.btnLoginLogin.visible()
        }
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(this, text, Toasty.LENGTH_LONG).show()
        }
    }

}