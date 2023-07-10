package mp.app.calonex.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import io.reactivex.Observable
import mp.app.calonex.common.apiCredentials.FirebaseCredentials
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.tenant.response.ResponseDtoResponse

class FirebaseService : IntentService("BackgroundIntentService") {

    private val LOG_TAG = "IntentFirebaseService"


    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "onCreate")
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(LOG_TAG, "onHandleIntent")

        persistFirebaseToken()
    }

    @SuppressLint("CheckResult")
    private fun persistFirebaseToken() {

        val credentials = FirebaseCredentials()

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(object :
            OnSuccessListener<InstanceIdResult> {
            override fun onSuccess(instanceIdResult: InstanceIdResult) {
                credentials.firebaseToken = instanceIdResult.token
                credentials.userId = Kotpref.userId

                val firebaseService: ApiInterface =
                    ApiClient(this@FirebaseService).provideService(ApiInterface::class.java)
                val apiCall: Observable<ResponseDtoResponse> =
                    firebaseService.persistFireBaseToken(credentials) //Test API Key

                RxAPICallHelper().call(apiCall, object : RxAPICallback<ResponseDtoResponse> {
                    override fun onSuccess(response: ResponseDtoResponse) {
                        Log.d(LOG_TAG, "Token Sent")

                    }

                    override fun onFailed(t: Throwable) {
                        Log.d(LOG_TAG, "Failed")
                    }
                })
            }
        })


    }


}