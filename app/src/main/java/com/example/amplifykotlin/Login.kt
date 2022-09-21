package com.example.amplifykotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.amplifyframework.core.Amplify

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun signIn(view: View){
        showLoader(true)
        val username=findViewById<EditText>(R.id.email)
        val password=findViewById<EditText>(R.id.password)

        Amplify.Auth.signIn(username.text.toString(), password.text.toString(),
            { result ->
                if (result.isSignInComplete) {
                    Log.i("MyAmplifyApp", "Sign in succeeded")
                    showLoader(false)
                    val intent = Intent(this, Main::class.java)
                    startActivity(intent)
                } else {
                    Log.i("MyAmplifyApp", "Sign in not complete")
                    showLoader(false)
                }
            },
            {
                Log.e("MyAmplifyApp", "Failed to sign in", it)
                showLoader(false)
            }
        )
    }

    fun showLoader(show: Boolean){
        val loader = findViewById<ProgressBar>(R.id.progressBar)
        val loginButton = findViewById<Button>(R.id.loginButton)

        if(show){
            loginButton.visibility = View.INVISIBLE
            loader.visibility = View.VISIBLE
        }else{
            loader.visibility = View.INVISIBLE
            loginButton.visibility = View.VISIBLE
        }
    }
}