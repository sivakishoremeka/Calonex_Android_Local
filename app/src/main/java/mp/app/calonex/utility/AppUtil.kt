package mp.app.calonex.utility

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


object AppUtil {


    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    private val SECOND_MILLIS: Long = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS
    private val YEAR_MILLIS = 365 * DAY_MILLIS
    fun getTimeAgo(date: Date): String {
        var time = date.time
        if (time < 1000000000000L) {
            time *= 1000
        }

        val now = currentDate().time
        //Log.e("now", "now>> $now")
        //Log.e("date", "date>> $date")
        //Log.e("time", "time>> $time")

        if (time > now || time <= 0) {
            return "in the future"
        }

        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> "Today" //Moments Ago
            diff < 2 * MINUTE_MILLIS -> "Today" //"a minute ago"
            diff < 60 * MINUTE_MILLIS -> "Today" //"${diff / MINUTE_MILLIS} minutes ago"
            diff < 2 * HOUR_MILLIS -> "Today" //"an hour ago"
            diff < 24 * HOUR_MILLIS -> "Today" //"${diff / HOUR_MILLIS} hours ago"
            diff < 48 * HOUR_MILLIS -> ""
            diff < 365 * DAY_MILLIS -> ""
            diff < 2 * YEAR_MILLIS -> ""
            else -> ""
        }
    }


}

