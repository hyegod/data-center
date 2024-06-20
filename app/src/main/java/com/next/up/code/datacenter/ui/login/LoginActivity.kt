package com.next.up.code.datacenter.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import com.inyongtisto.myhelper.base.BaseActivity
import com.inyongtisto.myhelper.extension.toastInfo
import com.jakewharton.rxbinding2.widget.RxTextView
import com.next.up.code.core.data.Resource
import com.next.up.code.core.session.BaseSharedPreferences.PREFERENCES_NAME
import com.next.up.code.core.session.BaseSharedPreferences.PREF_ID_USER
import com.next.up.code.core.session.BaseSharedPreferences.PREF_LOGIN_STATUS
import com.next.up.code.core.session.BaseSharedPreferences.PREF_ROLE
import com.next.up.code.core.session.BaseSharedPreferences.PREF_TOKEN
import com.next.up.code.core.session.BaseSharedPreferences.PREF_USERNAME
import com.next.up.code.datacenter.HomeActivity
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.databinding.ActivityLoginBinding
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        root = binding?.root
        setContentView(root)
        setupMainButton()
        validationStream()
        preferencesSetup()
        checkSession()
    }


    private fun preferencesSetup() {
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    private fun savePreferencesValue(idUser: String, role: String, name: String, token: String) {
        editor?.putString(PREF_ID_USER, idUser)
        editor?.putString(PREF_TOKEN, token)
        editor?.putString(PREF_USERNAME, name)
        editor?.putString(PREF_ROLE, role)
        editor?.putBoolean(PREF_LOGIN_STATUS, true)
        editor?.apply()
    }

    private fun checkSession() {
        try {
            if (sharedPreferences.getBoolean(PREF_LOGIN_STATUS, false)) {
                TOKEN_KEY = sharedPreferences.getString(PREF_TOKEN, "").toString()
                moveIntent()
            }
        } catch (_: Exception) {

        }
    }

    private fun setupMainButton() {
        binding?.apply {

            btnLogin.setOnClickListener { login() }
        }
    }


    private fun login() {
        val email = binding?.edtEmail?.text.toString()
        val password = binding?.edtPassword?.text.toString()
        viewModel.login(email, password).observe(this) { responseData ->
            when (responseData) {
                is Resource.Success -> {
                    progress.dismiss()
                    val data = responseData.data
                    TOKEN_KEY = data?.token.toString()
                    data?.let {
                        savePreferencesValue(
                            it.id.toString(),
                            "",
                            data.name,
                            data.token
                        )
                    }
                    moveIntent()
                }

                is Resource.Loading -> {
                    progress.show()
                }

                is Resource.Error -> {
                    progress.dismiss()
                    toastInfo(responseData.message.toString())
                    Log.d("TAG", "login: ${responseData.message}")
                }
            }
        }
    }

    private fun moveIntent() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("CheckResult")
    private fun validationStream() {
        val emailStream = binding?.edtEmail?.let {
            RxTextView.textChanges(it).skipInitialValue()
                .map { email -> !Patterns.EMAIL_ADDRESS.matcher(email).matches() }
        }

        emailStream?.subscribe {
            showEmailExistAlert(it)
        }

        val passwordStream = binding?.edtPassword?.let {
            RxTextView.textChanges(it).skipInitialValue()
                .map { password -> password.isNullOrEmpty() }
        }

        passwordStream?.subscribe {
            showPasswordMinimalAlert(it)
        }

        val invalidStream = Observable.combineLatest(
            emailStream, passwordStream
        ) { t1: Boolean, t2: Boolean -> !t1 && !t2 }

        invalidStream.subscribe { isValid ->
            if (isValid) {
                binding?.btnLogin?.isEnabled = true
                binding?.btnLogin?.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.primaryColor
                    )
                )
            } else {
                binding?.btnLogin?.isEnabled = false
                binding?.btnLogin?.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.colorSecondary
                    )
                )

            }
        }
    }


    private fun showEmailExistAlert(isNotValid: Boolean) {
        binding?.ttlEmail?.helperText =
            if (isNotValid) getString(R.string.email_not_valid) else null
    }

    private fun showPasswordMinimalAlert(isNotValid: Boolean) {
        binding?.ttlPassword?.helperText =
            if (isNotValid) getString(R.string.password_not_valid) else null
    }

    companion object {
        var TOKEN_KEY = "token"
    }

}