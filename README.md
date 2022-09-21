## Amplify:

The Amplify open-source client libraries provide use-case centric, opinionated, declarative, and easy-to-use interfaces across different categories of cloud powered operations enabling mobile and web developers
to easily interact with their backends.

To be able to have access to use Amplify Libaries and function , first you need to be authenticated to provide the necessary authorization to the other Amplify Categories (Like Storage, API, ...),
or else you will get access denied.

## amplifyconfiguration.json file

This file is essential for amplify to specify the configuartion for each resource amplify will use.
The Amplify libraries support configuration through the amplifyconfiguration.json file which defines all the regions and service endpoints for your backend AWS resources.
Place this file in src/main/res/raw.


## awsconfiguration.json file

This file is essential for AWS AppSync SDK.
Place this file in src/main/res/raw.

## Amplify Authentication:

Amplify utilizes AWS cognito service from amazon for Authentication.
In Screens 'MainActivity' and 'Login' , we are using Amplify to sign up a user by providing his email, password, and unique username.
Then confirming his account by sending a confirmation code to his email (can be his phone number instead, or both) and confirming it from the app in 'Confirmation' screen.
We configured our identity pool to deny access to all services for unauthenticated users.

1. amplifyconfiguration.json:

```json
  "auth": {
        "plugins": {
            "awsCognitoAuthPlugin": {
                "CredentialsProvider": {
                    "CognitoIdentity": {
                        "Default": {
                             "PoolId": "[COGNITO IDENTITY POOL ID]",
                            "Region": "[REGION]"
                        }
                    }
                },
                "CognitoUserPool": {
                    "Default": {
                         "PoolId": "[COGNITO USER POOL ID]",
                        "AppClientId": "[COGNITO USER POOL APP CLIENT ID]",
                        "Region": "[REGION]"
                    }
                }
            }
        }
    }
```

2. Signup:

```java
val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), "USER_EMAIL")
            .build()
        Amplify.Auth.signUp("USERNAME", "PASSWORD, options,
            {
                Log.i("AuthQuickStart", "Sign up succeeded: $it")
            },
            {
                Log.e ("AuthQuickStart", "Sign up failed", it)
            }
        )
```

3. Confirmation:

```java
 Amplify.Auth.confirmSignUp("USERNAME", "CONFIRMATION_CODE",
            { result ->
                if (result.isSignUpComplete) {
                    Log.i("AuthQuickstart", "Confirm signUp succeeded")
                } else {
                    Log.i("AuthQuickstart","Confirm sign up not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
        )
```

4. Login:

```java
 Amplify.Auth.signIn("USERNAME", "PASSWORD",
            { result ->
                if (result.isSignInComplete) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                }
            },
            {
                Log.e("AuthQuickstart", "Failed to sign in", it)
            }
        )
```

After Successfull login, you can now use other services from Amplify

## Amplify Storage:

Amplify Storage utilizes Amazon S3 resource which provides you the ability to upload and download object files of different types.
In 'amplifyconfiguration.json' you need to specify the bucket name and regoin where your files will be uploaded

1. amplifyconfiguration.json:

```json
    "storage": {
        "plugins": {
            "awsS3StoragePlugin": {
                 "bucket": "[BUCKET NAME]",
                  "region": "[REGION]"
            }
        }
    }
```

2. Upload file:

When adding the Storage category, you configure the level of access users have to your S3 bucket.
There are three levels for access:

- Guest: Accessible by all users of your application
- Protected: Readable by all users, but only writable by the creating user
- Private: Readable and writable only by the creating user

here we are using the Protected level of access

```java
        val options = StorageUploadFileOptions.builder()
            .accessLevel(StorageAccessLevel.PROTECTED)
            .build()

        val exampleFile = File(applicationContext.filesDir, "ExampleKey")
        exampleFile.writeText("Example file contents")

        Amplify.Storage.uploadFile("FILE_NAME", exampleFile,options,
            { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}") },
            { Log.e("MyAmplifyApp", "Upload failed", it) }
        )
```

so when uploaded it will be available inside [BUCKET_NAME]/protected/[USER_IDENTITY_ID]/[FILE_NAME]
[USER_IDENTITY_ID] is specific to each user and will be automatically associated to the bucket when choosing Protected level
If we remove the option param, all files for all users will be uploaded in public folder
example: [BUCKET_NAME]/public/[FILE_NAME]

3. List File:

```java
   Amplify.Storage.list("",
                   { result ->
                       result.items.forEach { item ->
                           Log.i("MyAmplifyApp", "Item: ${item.toString()}")
                       }
                   },
                   { Log.e("MyAmplifyApp", "List failure", it) }
               )
```

This will retreive all publice files to the user.

To list the files specific to this user, we need to retrieve the user's Identity Id and provide it in the options field
So we need [USER IDENTITY ID] to access the files.
Use ```java Amplify.Auth.fetchAuthSession()``` to fetch the current auth session and get the identityId for the user

```java
            Amplify.Auth.fetchAuthSession(
           {

               val session = it as AWSCognitoAuthSession
               identityId = session.identityId.value.toString()

               val options = StorageListOptions.builder()
                   .accessLevel(StorageAccessLevel.PROTECTED)
                   .targetIdentityId(identityId)
                   .build()

               Amplify.Storage.list("",options,
                   { result ->
                       result.items.forEach { item ->
                           Log.i("MyAmplifyApp", "Item: ${item.toString()}")
                       }
                   },
                   { Log.e("MyAmplifyApp", "List failure", it) }
               )
           },
           { error -> Log.e("AmplifyQuickstart", "Failed to fetch auth session", error) }
        )
```

## Amplify API (REST):

The Amplify API category provides an interface for making requests to your backend.
You need to have and API Gateway configures in AWS

1. amplifyconfiguration.json:

```json
 "api": {
        "plugins": {
            "awsAPIPlugin": {
                "[API NAME]": {
                    "endpointType": "REST",
                    "endpoint": "[API GATEWAY ENDPOINT]",
                    "region": "[REGION]",
                    "authorizationType": "[AUTHORIZATION TYPE]",
                    ...
                }
            }
        }
    }
```

2. Get request:

```java
        val options = RestOptions.builder()
            .addPath("/helloworld")
            .build()

        Amplify.API.get(options,
            { Log.i("MyAmplifyAPI", "GET succeeded: $it") },
            { Log.e("MyAmplifyAPI", "GET failed", it) }
        )
```

## AppSync (Realtime):

For this we are using 'AWS AppSync SDK for Android' https://github.com/awslabs/aws-mobile-appsync-sdk-android.
The AWS AppSync SDK for Android enables you to access your AWS AppSync backend and perform operations like queries, mutations, and subscription.
You need to have an app on AWS Appsync and define your schema, mutation, queries, and subscriptions which will store these data in DynamoDB.
you have to add the schema and graphql in src/main/graphql.

1. awsConfiguarion.json:

- AuthMode:
  When making calls to AWS AppSync, there are several ways to authenticate those calls (AWS_IAM, API_KEY, AMAZON_COGNITO_USER_POOLS, OPENID_CONNECT)
  In our case we need to use AWS_IAM, so based on the Cognito authenticated and unauthenticated roles and permissions, the user will have access or not to use AppSync functionality.

```json
{
  "AppSync": {
    "Default": {
      "ApiUrl": "YOUR-GRAPHQL-ENDPOINT",
      "Region": "[REGION]",
      "AuthMode": "AUTH_MODE"
    }
  }
}
```

2. ClientFactory:

This file will basicly initialize the AWSAppSync client in order to use it in our application

'AWSConfiguration' is basically the configuartion, specified in awsConfiguarion.json, required to create a AWSAppSyncClient object.

```java
            val awsConfig = AWSConfiguration(context)

            val credentialsProvider = CognitoCachingCredentialsProvider(context, awsConfig)

            client = AWSAppSyncClient.builder()
                .context(context!!)
                .awsConfiguration(awsConfig)
                .credentialsProvider(credentialsProvider)
                .build()
```

And then we need to initalize the client in our onCreate function where we will use it

```java
       override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main2)

         ClientFactory.init(this)
    }
```

2. Add Modal:

This will use the CreateMyModelTypeMutation we initialized in our AppSync backend.

```java
            val modalCallback: GraphQLCall.Callback<CreateMyModelTypeMutation.Data?> =
            object : GraphQLCall.Callback<CreateMyModelTypeMutation.Data?>() {
                fun onResponse(response: Response<CreateMyModelTypeMutation.Data?>?) {
                    Log.i("AWSAppSyncAdd", "succeeded: $response")
                }

                override fun onFailure(failure: ApolloException) {
                    Log.i("AWSAppSyncAdd", "failure: $failure")
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<CreateMyModelTypeMutation.Data?>) {
                    Log.i("AWSAppSyncAdd", "succeeded: $response")
                }
            }

        val createModalInput: CreateMyModelTypeInput = CreateMyModelTypeInput.builder()
            .title("title")
            .description("hello this is description")
            .build()

        val addModalMutation: CreateMyModelTypeMutation =
            CreateMyModelTypeMutation.builder().input(createModalInput).build()

        ClientFactory.appSyncClient()?.mutate(addModalMutation)?.enqueue(modalCallback);
```

3. List Modals:

This will use the ListMyModelTypesQuery we initialized in our AppSync backend.

```java
           val modalCallback: GraphQLCall.Callback<ListMyModelTypesQuery.Data?> =
            object : GraphQLCall.Callback<ListMyModelTypesQuery.Data?>() {
                              override fun onFailure(failure: ApolloException) {
                    Log.i("AWSAppSyncList", "failure: $failure")
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<ListMyModelTypesQuery.Data?>) {
                    val data = response.data()?.listMyModelTypes()
                    Log.i("AWSAppSyncList", "succeeded: ${data.toString()}")
                }
            }
        val listModalInput: ListMyModelTypesQuery = ListMyModelTypesQuery.builder().build()

        ClientFactory.appSyncClient()?.query(listModalInput)?.enqueue(modalCallback)
```

4. Subscription:

In our AppSync console, we created a subscription type on our 'CreateMyModelType' mutation, we called it 'MySubscription'

In subscriptions.graphql:

```
       subscription MySubscription {
          onCreateMyModelTypeV2 {
            description
            id
            title
          }
        }
```

In Main.kt:

```java

    fun subscribeModal(view: View) {
        val subscription: MySubscription = MySubscription.builder().build()
        subscriptionWatcher = ClientFactory.appSyncClient()?.subscribe(subscription)
        subscriptionWatcher?.execute(subCallback)
    }

    private val subCallback: AppSyncSubscriptionCall.Callback<MySubscription.Data?> =
        object : AppSyncSubscriptionCall.Callback<MySubscription.Data?> {

            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("Subscription", e.toString())
            }

            override fun onCompleted() {
                Log.i("Subscription", "Subscription completed")
            }

            override fun onResponse(response: com.apollographql.apollo.api.Response<MySubscription.Data?>) {
                runOnUiThread {
                    Log.i("AWSAppSyncSubscribed", "Subscription response: " + response.data().toString())
                }
            }
        }
```

Now when we call subscribeModal, we will be subscribed to the onCreate onCreateMyModelType mutation, so whenever a new modal is added,
the onResponse will be triggered.

You can also add subscription to a specific ID of a modal and when UpdateMyModelType mutation is called on that Id your subscription will be triggered

```
      subscription OnUpdateMyModelType(
         $id: ID
        ) {
          onUpdateMyModelType(id: $id) {
            id
            title
            description
          }
       }
```
