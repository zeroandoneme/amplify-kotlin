package com.example.amplifykotlin

import android.content.Context
import kotlin.jvm.Volatile
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import kotlin.jvm.Synchronized
import com.example.amplifykotlin.ClientFactory
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobile.auth.userpools.CognitoUserPoolsSignInProvider
import com.amazonaws.mobile.auth.core.IdentityManager
import com.amazonaws.mobileconnectors.appsync.sigv4.BasicAPIKeyAuthProvider
import com.amazonaws.mobileconnectors.appsync.sigv4.BasicCognitoUserPoolsAuthProvider
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.auth.CognitoCachingCredentialsProvider




object ClientFactory {
    @Volatile
    private var client: AWSAppSyncClient? = null
    @Synchronized

    fun init(context: Context?) {
        if (client == null) {
            val awsConfig = AWSConfiguration(context)

            val credentialsProvider = CognitoCachingCredentialsProvider(context, awsConfig)

            client = AWSAppSyncClient.builder()
                .context(context!!)
                .awsConfiguration(awsConfig)
                .credentialsProvider(credentialsProvider)
                .build()
        }
    }

    @Synchronized
    fun appSyncClient(): AWSAppSyncClient? {
        return client
    }
}
