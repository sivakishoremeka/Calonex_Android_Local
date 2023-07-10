package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
import mp.app.calonex.app.CxApplication
import mp.app.calonex.common.apiCredentials.Credentials
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.landlord.model.User
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkEvent
import mp.app.calonex.common.utility.NetworkState
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

abstract public class CxBaseActivity : AppCompatActivity() {

    lateinit var toast: Toast

    lateinit protected var cxBaseActivity: CxBaseActivity
    protected var gpApplication: CxApplication? = null

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mRunnable: Runnable = Runnable { alertLogout2() }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        cxBaseActivity = this
        gpApplication = application as CxApplication

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    override fun onResume() {
        super.onResume()
        NetworkEvent.register(this, Consumer {
            when (it) {
                NetworkState.NO_INTERNET -> displayErrorDialog(
                    "no internet",
                    "PLease check Internet Connection"
                )

                NetworkState.NO_RESPONSE -> displayErrorDialog(
                    "no response",
                    "no response came"
                )

                NetworkState.UNAUTHORISED -> {
                    //redirect to login screen - if session expired
                    // Auto Login

                    var credentials = Credentials()
                    credentials.emailId = Kotpref.emailId
                    credentials.password = Kotpref.password
                    credentials.deviceId = Kotpref.firebaseToken


                    val interceptor = HttpLoggingInterceptor()
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                    val client = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .addInterceptor(interceptor)
                        .build()

                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://api.calonex.com/api/")
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    //service = retrofit.create(RestInterface::class.java)


                    val apiCall = retrofit.create(ApiInterface::class.java)

                    val call = apiCall.login(credentials)
                    call.enqueue(object : Callback<User> {
                        override fun onResponse(
                            call: Call<User>,
                            response: Response<User>
                        ) {
                            try {
                                Kotpref.accessToken = response.body()!!.access_token

                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {

                        }
                    })

                    /*

                    val loginService: ApiInterface =
                        ApiClient(this).provideService(ApiInterface::class.java)
                    val apiCall: Observable<User> =
                        loginService.login(credentials)

                    apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                        Log.e("onSuccess", it.userName)
                        Kotpref.accessToken=it.access_token
                    },
                        {  e ->
                            Log.e("onFailure",e.toString())
                        })*/
                }
            }
        })
    }

    /*
     * Step 2: we unregister the activity once it is
     * finished.
     */
    override fun onStop() {
        super.onStop()
        NetworkEvent.unregister(this)
        stopHandler()
    }

    private fun alertLogout() {
        val builder = AlertDialog.Builder(applicationContext)
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.tag_logout))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            Kotpref.isLogin = false
            //startActivity(Intent(applicationContext, LoginScreen::class.java))
            val intent = Intent(applicationContext, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }


    /*
     * Step 3: We are just displaying an error
     * dialog here! But you configure whatever
     * you want
     */
    fun displayErrorDialog(
        title: String,
        desc: String
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(desc)
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .show()
    }

    fun showToast(any: Any) {
        if (any == null)
            return
        if (toast != null)
            toast.cancel()
        toast = Toast.makeText(cxBaseActivity, any.toString(), Toast.LENGTH_SHORT)
        toast.show()


    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        stopHandler()
        startHandler()
    }

    fun startHandler() {
        mHandler.postDelayed(mRunnable, StatusConstant.mTime)
    }

    fun stopHandler() {
        mHandler.removeCallbacks(mRunnable)
    }

    private fun alertLogout2() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.logout))
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.logout_msg))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            Kotpref.isLogin = false
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            startHandler()
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopHandler()
    }
}