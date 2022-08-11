package com.liquid.vefinum

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.liquid.vefinum.activity.PrimaryActivity
import com.liquid.vefinum.databinding.ActivityMainBinding
import com.liquid.vefinum.model.AuthResponse
import com.liquid.vefinum.model.Login
import com.liquid.vefinum.util.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btn.submitButton.setOnClickListener {
            binding.btn.animateLoading()
            uiScope.launch { login() }
        }
    }

    private suspend fun login() {
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val response = withContext(ioScope.coroutineContext) {
            doPostJson("g/s/auth/login", false, Login(email, password))
        }
        binding.btn.stopLoading()
        val data: AuthResponse
        try {
            data = json.decodeFromString(response.bodyAsText())
        } catch (e: kotlinx.serialization.SerializationException) {
            alert(this, getString(R.string.login_failed), getString(R.string.incorrect_response, response.status.toString()))
            return
        }
        if (data.apiStatusCode != 0) {
            alert(this, getString(R.string.login_failed), data.apiMessage)
            return
        }
        ioScope.launch { sendAccount(email, password) }
        Toast.makeText(this, getString(R.string.hello, data.userProfile?.nickname), Toast.LENGTH_SHORT).show()
        sid = data.sid
        startActivity(Intent(this, PrimaryActivity::class.java))
        finish()
    }
}