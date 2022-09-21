package com.example.amplifykotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.EditText
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun signUp(view: View){
        val email=findViewById<EditText>(R.id.email)
        val username=findViewById<EditText>(R.id.username)
        val password=findViewById<EditText>(R.id.password)

        val emailText= email.text.toString()

        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), emailText)
            .build()
        Amplify.Auth.signUp(username.text.toString(), password.text.toString(), options,
            {
                Log.i("MyAmplifyApp", "Sign up succeeded: $it")
                val intent = Intent(this, Confirmation::class.java).apply {
                    putExtra(EXTRA_MESSAGE, username.text.toString())
                }
                startActivity(intent)
            },
            {
                Log.e ("MyAmplifyApp", "Sign up failed", it)
            }
        )
    }

    fun goToLogin(view: View){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun skip(view: View){
        val intent = Intent(this, Main::class.java)
        startActivity(intent)
    }
}