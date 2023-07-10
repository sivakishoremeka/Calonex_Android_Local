package mp.app.calonex.landlord.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import mp.app.calonex.R
import mp.app.calonex.app.CxApplication
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.common.utility.Kotpref

abstract class CxBaseActivity2  : AppCompatActivity() {

    lateinit protected var cxBaseActivity: CxBaseActivity2
    protected var gpApplication: CxApplication? = null

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mRunnable: Runnable = Runnable { alertLogout() }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        try {
            cxBaseActivity = this
            gpApplication = application as CxApplication
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (e: Exception) {
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        stopHandler()
        startHandler()
    }

    fun startHandler(){
        mHandler.postDelayed(mRunnable, StatusConstant.mTime)
    }

    fun stopHandler(){
        mHandler.removeCallbacks(mRunnable)
    }

    private fun alertLogout() {
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

    override fun onStop() {
        super.onStop()
        stopHandler()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopHandler()
    }


    fun checkForFilePermission(requesCode: Int): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            cxBaseActivity!!,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        val writePermissionCheck = ContextCompat.checkSelfPermission(
            cxBaseActivity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED && writePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                requesCode
            )
            return false
        }
        return true
    }

}