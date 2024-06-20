package com.next.up.code.datacenter

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.databinding.ActivityMainBinding
import com.next.up.code.datacenter.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private var root: View? = null

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        root = binding?.root
        setContentView(root)

        preferencesSetup()
        val isLogin =
            sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()

        Handler(Looper.getMainLooper()).postDelayed({
            if (isLogin.isNotEmpty()) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }


    private fun preferencesSetup() {
        sharedPreferences = getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME,
            MODE_PRIVATE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}