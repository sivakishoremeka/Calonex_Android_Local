package mp.app.calonex.common.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mp.app.calonex.R
import mp.app.calonex.landlord.model.StatesModel
import mp.app.calonex.landlord.model.ZipCityStateModel
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Util {


    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()

    }

    fun isNullOrEmpty(str: String?): Boolean {
        if (str != null && !str.isEmpty())
            return false
        return true
    }


    fun alertYesMessage(context: Context, title: String?, msg: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
        }


        builder.show()
    }

    fun alertOkMessage(context: Context, title: String?, msg: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }


    fun alertOkIntentMessage(
        activityHost: Activity,
        title: String?,
        msg: String?,
        target: Class<*>?
    ) {
        val builder = AlertDialog.Builder(activityHost)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton(activityHost.getString(R.string.ok)) { dialog, which ->
            val intent = Intent(activityHost, target)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activityHost.startActivity(intent)
            activityHost.overridePendingTransition(R.anim.activity_in, R.anim.activity_out)
            activityHost.finish()
            dialog.dismiss()
        }
        builder.show()
    }

    fun getDateTime(timestamp: String): String? {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp.toLong()
        val valueDate: String = DateFormat.format("MMM dd, yyyy", cal).toString()

        return valueDate
    }


    fun getDateAndTime(s: String): String? {
        try {
            val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy' 'HH:mm")
            return simpleDateFormat.format(s.toLong())
        } catch (e: Exception) {
            return e.toString()
        }
    }

    // August 28 2020
    fun getDateTimeStamp(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MMMM dd yyyy")
            val netDate = Date(s.toLong())
            netDate.month
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun getDateTimeStamp(s: String, format: String): String? {
        try {
            val sdf = SimpleDateFormat(format)

            /*Date date = (Date)formatter.parse(str_date);
System.out.println("Today is " +date.getTime());*/
            val netDate = sdf.parse(s)

            return "" + netDate.time
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun getMonthStamp(s: String): Int {
        try {
            val sdf = SimpleDateFormat("MMMM dd yyyy")
            val netDate = Date(s.toLong())

            return netDate.month
        } catch (e: Exception) {
            return 0
        }
    }

    fun getYearStamp(s: String): Int {
        try {
            val sdf = SimpleDateFormat("MMMM dd yyyy")
            val netDate = Date(s.toLong())

            return netDate.year - 100
        } catch (e: Exception) {
            return 0
        }
    }


    fun navigationNext(activity: Activity, target: Class<*>?) {
        activity.startActivity(Intent(activity, target))
        activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out)
    }

    fun navigationNext(activity: Activity) {

        activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out)
    }

    fun navigationBack(activity: Activity, target: Class<*>?) {
        activity.startActivity(Intent(activity, target))
        activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out)
    }

    fun navigationBack(activity: Activity) {
        activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out)
    }

    fun apiFailure(context: Context, t: Throwable) {
        val exception: HttpException = t as HttpException
        if (exception.code().equals(401)) {
            Toast.makeText(
                context,
                context.getString(R.string.error_invalid_cred),
                Toast.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(context, context.getString(R.string.error_server), Toast.LENGTH_SHORT)
                .show()

        }

    }

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

//    fun getStateList(context: Context, fistValue: String):ArrayList<String>{
//
//        var stateList=ArrayList<String>()
//
//        val jsonFileString = getJsonDataFromAsset(context, "states.json")
//        val gson = Gson()
//        val listState = object : TypeToken<List<StatesModel>>() {}.type
//
//        var listStates: List<StatesModel> = gson.fromJson(jsonFileString, listState)
//        stateList.add(fistValue)
//        for(item in listStates){
//            stateList.add(item.name.toString())
//        }
//
//        return stateList
//
//    }

    fun getZipCityStateList(context: Context): ArrayList<ZipCityStateModel> {

        var zcsList = ArrayList<ZipCityStateModel>()

        val jsonFileString = getJsonDataFromAsset(context, "zcs.json")
        val gson = Gson()
        val listState = object : TypeToken<List<ZipCityStateModel>>() {}.type

        var listStates: List<StatesModel> = gson.fromJson(jsonFileString, listState)
        zcsList = listStates as ArrayList<ZipCityStateModel>


        return zcsList

    }

    fun valueMandetory(context: Context, value: String?, editValue: EditText): Boolean {
        if (value.isNullOrEmpty()) {
            Toast.makeText(
                context,
                editValue.hint.toString() + " is a mandatory field.",
                Toast.LENGTH_SHORT
            ).show()
            editValue?.error = context.getString(R.string.error_mandetory)
            editValue?.requestFocus()
            return true
        }
        return false
    }


    fun isDateValid(dateStr: String, myFormat: String?): Boolean {

        val sdf: java.text.DateFormat = SimpleDateFormat(myFormat)
        sdf.setLenient(false)
        try {
            sdf.parse(dateStr)
        } catch (e: ParseException) {
            return false
        }
        return true
    }


    fun convertLongToTime(time: Long, myFormat: String?): String {
        val date = Date(time)
        val format = SimpleDateFormat(myFormat)
        return format.format(date)
    }

    fun setEditReadOnly(editText: TextInputEditText, value: Boolean, input: Int) {
        editText.isFocusable = value
        editText.isFocusableInTouchMode = value
        editText.inputType = input
    }

    fun convertDateToLong(date: String, myFormat: String?): Long {
        val df = SimpleDateFormat(myFormat)
        return df.parse(date).time
    }

    fun firstTwo(str: String): String {
        if (str.length < 2) {
            return ""
        }
        return str.substring(0, 2).toUpperCase()
    }

    fun firstLetterWord(str: String): String? {
        var result = ""

        // Traverse the string.
        var v = true
        for (i in 0 until str.length) {
            // If it is space, set v as true.
            if (str[i] == ' ') {
                v = true
            } else if (str[i] != ' ' && v == true) {
                result += str[i]
                v = false
            }
        }
        return result
    }


    fun getLeaseStatus(str: String): String {
        if (str.equals("11")) {
            return "SUBMITTED BY AGENT"
        } else if (str.equals("12")) {
            return "SUBMITTED BY BROKER"
        } else if (str.equals("13")) {
            return "REJECTED BY LANDLORD"
        } else if (str.equals("14")) {
            return "REJECTED BY TENANT"
        } else if (str.equals("15")) {
            return "TENANT SIGNATURE PENDING"
        } else if (str.equals("16")) {
            return "TENANT SIGNATURE INPROGRESS"
        } else if (str.equals("17")) {
            return "LANDLORD APPROVAL PENDING"
        } else if (str.equals("18")) {
            return "LANDLORD SIGNATURE PENDING"
        } else if (str.equals("19")) {
            return "FINALIZED"
        } else if (str.equals("20")) {
            return "NOT STARTED"
        } else if (str.equals("21")) {
            return "INPROGRESS"
        } else if (str.equals("22")) {
            return "SIGNED"
        } else if (str.equals("23")) {
            return "TERMINATED"
        } else if (str.equals("24")) {
            return "TERMINATED & REFUND PENDING"
        }/*else if (str.equals("25") || str.equals("26") ||str.equals("27")) {
                return "LANDLORD APPROVED"

        }*/ else if (str.equals("28")) {
            return "REJECTED DEADLINE EXCEEDED"
        } else if (str.equals("29")) {
            return "TERMINATED & SECURITY REFUNDED"
        } else if (str.equals("30")) {
            return "CANCELLED"
        } else if (str.equals("31")) {
            return "REFUND FAILED"
        } else if (str.equals("32")) {
            return "SUBMITTED FOR RENEWAL"
        }
        else if (str.equals("33")) {
            return "TERMINATED"
        }
        else {
            return "DND"
        }

        return ""
    }

    fun getRealPathFromUri(activity: Activity, contentUri: Uri): File? {

        //create a file to write bitmap data
        var file: File? = null
        var path: String? = ""
        val cursor =
            activity!!.contentResolver.query(contentUri, null, null, null, null)

        if (cursor == null) {
            path = contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            path = cursor.getString(index)
        }
        file = File(path)
        return file

    }

    fun bitmapToFile(
        activity: Activity,
        bitmap: Bitmap,
        fileNameToSave: String
    ): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(activity.filesDir, fileNameToSave + ".jpeg")
            file!!.createNewFile()
            val newBitmap =
                Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(newBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            //write the bytes in file
            val fos: OutputStream = FileOutputStream(file)
            //val b2 = Bitmap.createScaledBitmap(bitmap, 400, 600, false)
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos) // YOU can also save it in JPEG
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    fun customProgress(context: Context): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        return circularProgressDrawable

    }


    fun setSocNum(x: String?) {
        if (Pattern.matches("^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$", x)) {
            println("please enter a valid social security number")
        }
        // further logic goes here
    }
}