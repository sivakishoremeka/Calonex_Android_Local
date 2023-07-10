package mp.app.calonex.app

//import androidx.room.Room
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import mp.app.calonex.landlord.activity.HomeActivityCx
import java.lang.Thread.UncaughtExceptionHandler


//import androidx.room.Room

class CxApplication : MultiDexApplication(), UncaughtExceptionHandler {

    private val TAG = "MyFirebaseToken"


    override fun onCreate() {
        /*StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork() // or .detectAll() for all detectable problems
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )*/

        super.onCreate()
//        database = Room.databaseBuilder(this, AppDatabase::class.java, "MyDatabase")
//            .allowMainThreadQueries().build()
        Kotpref.init(this)

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = "firebase token" + token
                Log.d(TAG, msg)
            })

    }



    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()

        val intent = Intent(this, HomeActivityCx::class.java)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = this.getSystemService(ALARM_SERVICE) as? AlarmManager
        alarmManager?.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, pendingIntent)

        System.exit(0)
    }
}

/*
// ERROR
CropImageView

// Code check
Upto landlord->activity [done]

item_subcription_plan.xml
bar_graph_fragment.xml

 Landlord Home chart colour fix

androidx.appcompat.widget.SwitchCompat (TenantListAdapter)



AddNewPropertyThirdScreen (error due to skip button of previous page)
PaymentHistoryLdFragment.kt:98:java.lang.NullPointerException


// UI scale
UserPropertyDetailScreen [fix number length issue]

Log.e("credentials_LOG", Gson().toJson(credentials))


isCraditCard

US pin code = 99501
Bank Account no = 000123456789
Routing no = 110000000
1st amount = 32
2nd amount = 45
Address = address_full_match

Card no = 4111 1111 1111 1111
CVC = 123
Postal code = 99501

// Stripe code
rsqu-lhfp-zber-gdwl-wjoe

Some test credentials:
Admin:
Email: admin@calonex.com
Password: Test@123

Broker:
Email: cx.broker@yopmail.com
Password: Test@123
Licence No: 98765432111

Agent:
Email: cx.agent@yopmail.com | agent_ios@yopmail.com
Password: Test@123
Licence No: 79787946464

Landlord:
Email: cx.landlord@yopmail.com
Password: Test@123

Tenant:
Email: cx.tenant@yopmail.com | calonex.new.tenant2@yopmail.com
Password: Test@123

 ----------------------------

Test publishable key
pk_test_51HxhDBKJ1orMwPHntkQXg8gw9lDCsthnwshMLWHQxZuq6qzmYGrjqhTFtXrehaXjt4jb0sqTLF1YMdhIqZCtNiU700y8yovFv3

Live publishable key
pk_live_51HxhDBKJ1orMwPHnhwizTvFdZj1VgjtaBoYIBiZQ808lVD3Uas2SyKUe56QxGDzrzI9jsRydX7s6qjB9r3MDqzCh00T6KbRPu0
*/

/*
Add skip bank now in account details page in register landlord page in agent and broker
check design with web
add tenant pay fee card pay in property request
AddNewPropertyThirdScreen [landlord->property->add unit] crash and finish button
tenant dashboard 2nd page graph label not showing

test landlord register response with web (card n bank)
fix rentcx
*/