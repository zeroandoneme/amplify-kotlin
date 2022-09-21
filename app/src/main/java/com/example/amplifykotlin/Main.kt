package com.example.amplifykotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.amazonaws.Response
import com.amazonaws.amplify.generated.graphql.CreateMyModelTypeMutation
import com.amazonaws.amplify.generated.graphql.ListMyModelTypesQuery
import com.amazonaws.amplify.generated.graphql.MySubscription
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageListOptions
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import type.CreateMyModelTypeInput

import java.io.File
import javax.annotation.Nonnull

class Main : AppCompatActivity() {

    private var subscriptionWatcher: AppSyncSubscriptionCall<MySubscription.Data>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        ClientFactory.init(this)
    }

    fun uploadFile(view: View) {
        val fileName=findViewById<EditText>(R.id.fileName)
      
        val options = StorageUploadFileOptions.builder()
            .accessLevel(StorageAccessLevel.PROTECTED)
            .build()

        val exampleFile = File(applicationContext.filesDir, "ExampleKey")
        exampleFile.writeText("Example file contents")

        Amplify.Storage.uploadFile(fileName.text.toString(), exampleFile,options,
            {
                Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}")
                Toast.makeText(applicationContext,"Successfully uploaded file",Toast.LENGTH_SHORT).show()
            },
            {
                Log.e("MyAmplifyApp", "Upload failed", it)
                runOnUiThread {Toast.makeText(applicationContext,"Something went wrong!",Toast.LENGTH_SHORT).show()}
            }
        )
    }

    fun ListFiles(view: View) {
        var identityId = "";
   
        Amplify.Auth.fetchAuthSession(
           {
               Log.i("MyAmplifyApp", "Auth session = $it")
               val session = it as AWSCognitoAuthSession
               identityId = session.identityId.value.toString()

               val options = StorageListOptions.builder()
                   .accessLevel(StorageAccessLevel.PROTECTED)
                   .targetIdentityId(identityId)
                   .build()

               Amplify.Storage.list("",options,
                   { result ->
                       var list = ""
                       result.items.forEach { item ->
                           Log.i("MyAmplifyApp", "Item: ${item.toString()}")
                           list=list.plus("${item.key.toString()}, ")
                       }
                       runOnUiThread {Toast.makeText(applicationContext,"files: ${list.toString()}",Toast.LENGTH_SHORT).show()}

                   },
                   {
                       Log.e("MyAmplifyApp", "List failure", it)
                       runOnUiThread {Toast.makeText(applicationContext,"Something went wrong!",Toast.LENGTH_SHORT).show()}
                   }
               )
           },
           {
                   error -> Log.e("MyAmplifyApp", "Failed to fetch auth session", error)
               runOnUiThread {Toast.makeText(applicationContext,"Something went wrong!",Toast.LENGTH_SHORT).show()}
           }
        )
    }

    fun fetchBackend(view: View){
        val options = RestOptions.builder()
            .addPath("/helloworld")
            .build()

        Amplify.API.get(options,
            {
                Log.i("MyAmplifyApp", "GET succeeded: ${it.toString()}")
                runOnUiThread {Toast.makeText(applicationContext,"Successfully got response!",Toast.LENGTH_SHORT).show()}
            },
            {
                Log.e("MyAmplifyApp", "GET failed", it)
                runOnUiThread {Toast.makeText(applicationContext,"Something went wrong!",Toast.LENGTH_SHORT).show()}
            }
        )
    }

    fun signOut(view: View){
        Amplify.Auth.signOut(
            {
                Log.i("MyAmplifyApp", "Signed out successfully")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            },
            {
                Log.e("MyAmplifyApp", "Sign out failed", it)
                runOnUiThread {Toast.makeText(applicationContext,"Something went wrong!",Toast.LENGTH_SHORT).show()}
            }
        )
    }

    fun addModal(view: View) {
        val modalCallback: GraphQLCall.Callback<CreateMyModelTypeMutation.Data?> =
            object : GraphQLCall.Callback<CreateMyModelTypeMutation.Data?>() {
                fun onResponse(response: Response<CreateMyModelTypeMutation.Data?>?) {
                    Log.i("MyAmplifyApp", "succeeded: $response")
                }

                override fun onFailure(failure: ApolloException) {
                    Log.i("MyAmplifyApp", "failure: $failure")
                    runOnUiThread {Toast.makeText(applicationContext,"Something went wrong!",Toast.LENGTH_SHORT).show()}
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<CreateMyModelTypeMutation.Data?>) {
                    Log.i("MyAmplifyApp", "succeeded: $response")
                }
            }

        val createModalInput: CreateMyModelTypeInput = CreateMyModelTypeInput.builder()
            .title("title")
            .description("hello this is description")
            .build()

        val addModalMutation: CreateMyModelTypeMutation =
            CreateMyModelTypeMutation.builder().input(createModalInput).build()

        ClientFactory.appSyncClient()?.mutate(addModalMutation)?.enqueue(modalCallback);

    }

    fun listModal(view: View) {
        val modalCallback: GraphQLCall.Callback<ListMyModelTypesQuery.Data?> =
            object : GraphQLCall.Callback<ListMyModelTypesQuery.Data?>() {
                override fun onFailure(failure: ApolloException) {
                    Log.i("MyAmplifyApp", "failure: $failure")
                    runOnUiThread {Toast.makeText(applicationContext,"Something went wrong!",Toast.LENGTH_SHORT).show()}
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<ListMyModelTypesQuery.Data?>) {
                    val data = response.data()?.listMyModelTypes()
                    Log.i("MyAmplifyApp", "succeeded: ${data.toString()}")
                }
            }
        val listModalInput: ListMyModelTypesQuery = ListMyModelTypesQuery.builder().build()

        ClientFactory.appSyncClient()?.query(listModalInput)?.enqueue(modalCallback)
    }

    fun subscribeModal(view: View) {
        Log.i("MyAmplifyApp", "Subscribe.....")
        val subscription: MySubscription = MySubscription.builder().build()
        subscriptionWatcher = ClientFactory.appSyncClient()?.subscribe(subscription)
        subscriptionWatcher?.execute(subCallback)
    }

    private val subCallback: AppSyncSubscriptionCall.Callback<MySubscription.Data?> =
        object : AppSyncSubscriptionCall.Callback<MySubscription.Data?> {

            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("MyAmplifyApp", e.toString())
                runOnUiThread {Toast.makeText(applicationContext,"Something went wrong!",Toast.LENGTH_SHORT).show()}
            }

            override fun onCompleted() {
                Log.i("MyAmplifyApp", "Subscription completed")
            }

            override fun onResponse(response: com.apollographql.apollo.api.Response<MySubscription.Data?>) {
                runOnUiThread {
                    Log.i("MyAmplifyApp", "Subscription response: " + response.data().toString())
                    runOnUiThread {Toast.makeText(applicationContext,"Value added!",Toast.LENGTH_SHORT).show()}
                }
            }
        }
}
