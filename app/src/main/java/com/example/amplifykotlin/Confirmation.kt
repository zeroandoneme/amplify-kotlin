package com.example.amplifykotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.EditText
import com.amplifyframework.core.Amplify

class Confirmation : AppCompatActivity() {
    var email = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)
        email = intent.getStringExtra(EXTRA_MESSAGE).toString()
    }

    fun confirmSignUp(view: View){
        val confirmationCode=findViewById<EditText>(R.id.confirmationCode)

        Amplify.Auth.confirmSignUp(
            email, confirmationCode.text.toString(),
            { result ->
                if (result.isSignUpComplete) {
                    Log.i("AuthQuickstart", "Confirm signUp succeeded")
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                } else {
                    Log.i("AuthQuickstart","Confirm sign up not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
        )
    }
}