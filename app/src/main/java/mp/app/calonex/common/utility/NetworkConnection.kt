package mp.app.calonex.common.utility

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

class NetworkConnection {

    companion object {
        @SuppressLint("MissingPermission")
        fun isNetworkConnected(context: Context): Boolean {

            val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectionManager.activeNetworkInfo

            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }
    }
}