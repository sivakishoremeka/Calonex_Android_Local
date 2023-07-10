package mp.app.calonex.common.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import mp.app.calonex.R
import mp.app.calonex.app.CxApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


abstract class CxBaseActivityKotlin : AppCompatActivity() {
    var validationForm: ValidationForm? = null
    protected var gpBaseActivity: CxBaseActivityKotlin? = null
    protected var gpApplication: CxApplication? = null
    protected var imageView: ImageView? = null
    var bundle: Bundle? = null
    private var fileUri: Uri? = null
    private lateinit var toast: Toast
    private var isCircular = false

    /**
     * function used to pick image from gallery so first check read permission at runtime.
     *
     * @param imageView
     * @param isCircularImage
     * @see this.checkForPermission
     */
    @JvmOverloads
    fun pickGalleryImage(
        imageView: ImageView?,
        isCircularImage: Boolean = false
    ) {
        this.imageView = imageView
        isCircular = isCircularImage
        if (checkForPermission(MY_PERMISSIONS_REQUEST_IMAGE)) {
            val pickGalleryImage = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            if (pickGalleryImage.resolveActivity(packageManager) != null) {
                startActivityForResult(
                    pickGalleryImage,
                    PICK_PICTURE_GALLERY
                )
            } else {
                showToast("No Gallery Application found..!!")
            }
        }
    }

    @JvmOverloads
    fun addCameraImage(
        imageView: ImageView?,
        isCircularImage: Boolean = false
    ) {
        this.imageView = imageView
        isCircular = isCircularImage
        if (checkForCameraPermission(MY_PERMISSIONS_REQUEST_CAMERA)) {
            captureImage()
        }
    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fileUri = getOutputMediaFileUri(FileColumns.MEDIA_TYPE_IMAGE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        // start the image capture Intent
        startActivityForResult(
            intent,
            CAMERA_CAPTURE_IMAGE_REQUEST_CODE
        )
    }

    override fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {

        // save file url in bundle as it will be null on scren orientation changes
        outState.putParcelable("file_uri", fileUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        fileUri = savedInstanceState.getParcelable("file_uri")
    }


    fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(
            getOutputMediaFile(
                type
            )
        )
    }

    fun readContacts() {
        if (checkForContactPermission(MY_PERMISSIONS_REQUEST_CONTACTS)) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.Contacts.CONTENT_TYPE
            startActivityForResult(
                intent,
                MY_PERMISSIONS_REQUEST_CONTACTS
            )
        }
    }

    fun selectFile(requestCode: Int) {
        val intent_upload = Intent()
        if (requestCode == MY_PERMISSIONS_REQUEST_AUDIO) intent_upload.type =
            "audio/*" else if (requestCode == MY_PERMISSIONS_REQUEST_VIDEO) intent_upload.type =
            "video/*"
        intent_upload.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            intent_upload,
            SELECT_CONTENT
        )
    }

    fun selectAudioFile() {
        if (!checkForPermission(MY_PERMISSIONS_REQUEST_AUDIO)) {
            return
        }
        selectFile(MY_PERMISSIONS_REQUEST_AUDIO)
    }

    fun selectVideoFile() {
        if (!checkForPermission(MY_PERMISSIONS_REQUEST_VIDEO)) {
            return
        }
        selectFile(MY_PERMISSIONS_REQUEST_VIDEO)
    }

    /**
     * function used to check the permission for read and write expternal storage
     * is permission not granted then showing dialog.
     *
     * @return
     */
    fun checkForPermission(requesCode: Int): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            gpBaseActivity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requesCode
            )
            return false
        }
        return true
    }

    fun checkForCameraPermission(requesCode: Int): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            gpBaseActivity!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                requesCode
            )
            return false
        }
        return true
    }

    fun checkForFilePermission(requesCode: Int): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            gpBaseActivity!!,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        val writePermissionCheck = ContextCompat.checkSelfPermission(
            gpBaseActivity!!,
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

    fun checkForContactPermission(requestCode: Int): Boolean {
        val permissionCheckContact = ContextCompat.checkSelfPermission(
            gpBaseActivity!!,
            Manifest.permission.READ_CONTACTS
        )
        if (permissionCheckContact != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                requestCode
            )
            return false
        }
        return true
    }

    /**
     * function used to check the permission for read Location
     * is permission not granted then showing dialog.
     *
     * @return
     */
    fun checkForLocationPermission(): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            gpBaseActivity!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
            return false
        }
        return true
    }

    fun checkForLocationPermissionUpdate(): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            gpBaseActivity!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    /**
     * function used to handel response according user
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_AUDIO, MY_PERMISSIONS_REQUEST_VIDEO -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                selectFile(requestCode)
            } else {
//                    showApologies();
            }
            MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED && imageView != null
            ) {
                addCameraImage(imageView)
            } else {
                imageView = null
            }
            MY_PERMISSIONS_REQUEST_IMAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED && imageView != null
            ) {
                pickGalleryImage(imageView)
            } else {
                imageView = null
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @Throws(Exception::class)
    fun setPic(imageView: ImageView, path: String?) {
        // Get the dimensions of the View
        val targetW = imageView.width
        val targetH = imageView.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor shl 1
        val bitmap = BitmapFactory.decodeFile(path, bmOptions)
        val mtx = Matrix()
        // mtx.postRotate(90);
        try {
            val ei = ExifInterface(path!!)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> mtx.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> mtx.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> mtx.postRotate(270f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Rotating Bitmap
        val rotatedBMP =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mtx, true)
        if (rotatedBMP != bitmap) {
            bitmap.recycle()
        }
        imageView.setImageBitmap(rotatedBMP)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    //    public boolean checkUserLogin() {
    //        if (getCurrentUser() == null) {
    //            FlavourUtil.loginPromptFragment(gpBaseActivity);
    //            return false;
    //        } else {
    //            return true;
    //        }
    //    }
    val isFullScreen: Boolean
        get() = false

    fun hasParent(): Boolean {
        return false
    }

    val heading: String
        get() = getString(R.string.app_name)

    val screenName: String
        get() = this.javaClass.simpleName

    fun getBundleString(key: String?, def: String): String {
        if (bundle == null) return def
        val value = bundle!!.getString(key)
        return value ?: def
    }

    fun setBundleString(key: String?, value: String?) {
        if (bundle == null) return
        bundle!!.putString(key, value)
    }

    fun getBundleLong(key: String?, def: Long): Long {
        return if (bundle == null) def else bundle!!.getLong(key)
    }

    fun getBundleInteger(key: String?, def: Int): Int {
        return if (bundle == null) def else bundle!!.getInt(key)
    }

    fun setBundleLong(key: String?, value: Long) {
        if (bundle == null) return
        bundle!!.putLong(key, value)
    }

    fun getBundleDouble(key: String?, dou: Double): Double {
        return if (bundle == null) dou else bundle!!.getDouble(key)
    }

    fun setBundleDouble(key: String?, value: Double) {
        if (bundle == null) return
        bundle!!.putDouble(key, value)
    }

    fun getBundleBoolean(key: String?): Boolean {
        return if (bundle == null) false else bundle!!.getBoolean(key)
    }

    fun setBundleBoolean(key: String?, value: Boolean) {
        if (bundle == null) return
        bundle!!.putBoolean(key, value)
    }

    fun getBundleString(key: String?): String {
        return getBundleString(key, "")
    }

    fun getBundleLong(key: String?): Long {
        return getBundleLong(key, 0)
    }

    fun getBundleInteger(key: String?): Int {
        return getBundleInteger(key, 0)
    }

    fun getBundleDouble(key: String?): Double {
        return getBundleDouble(key, 0.0)
    }

    fun handleBundle() {}
    fun handlePublishedObject(`object`: Any?) {}
    fun updateUI() {}
    val tag: String
        get() = javaClass.simpleName

    val orientation: Int
        get() = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    fun logout() {

//        launch(LoginScreen.class).clearStack().launchActivity();
    }

    fun noInternetPrompt() {
        showToast("Please make sure that you are connected to internet")
    }

    // the method is required to overridden in the activities
    val noDataDescription: String
        get() = "We advise that to discover relevant results please change your location"

    val noDataButton: String
        get() = "Retry"

    //    final public int getNoNetworkImage() {
    //        return R.drawable.empty_signall;
    //    }
    val noNetworkTitle: String
        get() = "No Network"

    //    final public String getNoNetworkDescription() {
    //        return "We are unable to reach our servers.\n\nPlease Check your internet settings..!!";
    //    }
    val noNetworkDescription: String
        get() = "Please make sure that you are connected to the network.\n\nPlease Check your internet settings..!!"

    val noNetworkButton: String
        get() = "Retry"

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        requestedOrientation = orientation
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (super.onOptionsItemSelected(item)) return true
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gpBaseActivity = this
        gpApplication = application as CxApplication
        bundle = intent.extras
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        bundle = getIntent().extras
        handleBundle()
        updateUI()
    }

    /**
     * function used to hide keyboard
     *
     * @param view
     */
    fun hideKeyBord(view: TextView) {
        val imm =
            (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    fun showApologies() {
        showToast("Something went wrong, Please try again.")
    }

    fun showToast(`object`: Any?) {
        if (`object` == null) return
        if (toast != null) toast!!.cancel()
        toast = Toast.makeText(gpBaseActivity, `object`.toString(), Toast.LENGTH_SHORT)
        toast.show()
    }

    fun showToastLong(`object`: Any?) {
        if (`object` == null) return
        if (toast != null) toast!!.cancel()
        toast = Toast.makeText(gpBaseActivity, `object`.toString(), Toast.LENGTH_LONG)
        toast.show()
    }

    /*public void showProgress(Object progressTag) {

        // check if the webview is available
        progressTime = System.currentTimeMillis();
        if (progress == null) {
            progress = View.inflate(this, R.layout.progress_simple, null);
            GpFontChanger.setFont(progress, GpFontChanger.JENNESUE, GpFontChanger.JENNESUE);
            addContentView(progress
                    , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                            , ViewGroup.LayoutParams.MATCH_PARENT));
            progress.setOnClickListener(null);
        } else {
//            progress.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn).duration(300).playOn(progress);
        }
        this.progressTag = progressTag;
    }

    // this is not used as of now
    public void showPagination(Object progressTag) {
        progressTime = System.currentTimeMillis();
        if (pagination == null) {
            pagination = View.inflate(this, R.layout.pagination, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
//            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            addContentView(pagination, lp);
//            pagination.setOnClickListener(null);
        } else {
            pagination.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn).duration(300).playOn(pagination);
        }
        this.progressTag = progressTag;
    }

    public void showSuccess(final boolean finish) {
        hideProgress();
        final View success;
        success = View.inflate(this, R.layout.success, null);
        addContentView(success
                , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT));
        success.setOnClickListener(null);
        YoYo.with(Techniques.SlideInDown).duration(300).playOn(success);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finish)
                    finish();
                else
                    success.setVisibility(View.GONE);
            }
        }, 1000);
    }*/
    /* public boolean isProgressing() {
         return (progress != null && progress.getVisibility() == View.VISIBLE)
                 || (pagination != null && pagination.getVisibility() == View.VISIBLE)
                 || (webview != null && webview.getVisibility() == View.VISIBLE);
     }
 */
    override fun onBackPressed() {
//        if (!hideProgress())
//            super.onBackPressed();
        super.onBackPressed()
    }

    /*public void hideView(View viewToHide) {
        if (viewToHide != null) {
            long t = System.currentTimeMillis() - progressTime;
            if (t < 1000)
                t = 1000 - t;
            else
                t = 0;
            YoYo.with(Techniques.FadeOut).delay(t).duration(500)
                    .withListener(new MyAnimationListener(viewToHide)).playOn(viewToHide);
        }
    }

    public boolean hideProgress() {
        if (isProgressing()) {
            GpServer.getInstance().cancel(progressTag);
            if (progress != null) {
                hideView(progress);
            }
            if (pagination != null) {
                hideView(pagination);
            }
            if (webview != null) {
                hideView(webview);
            }
            return true;
        } else
            return false;
    }*/
    override fun finish() {
        super.finish()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            PICK_PICTURE_GALLERY -> {
                // find the path
                val intent = Intent(gpBaseActivity, CropImageActivity::class.java)
                intent.data = data!!.data
                intent.putExtra("isCircular", false)
                startActivityForResult(
                    intent,
                    CROP_IMAGE_CODE
                )
            }
            CAMERA_CAPTURE_IMAGE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
//                    previewCapturedImage();
                val intent1 = Intent(gpBaseActivity, CropImageActivity::class.java)
                intent1.data = fileUri
                intent1.putExtra("isCircular", false)
                startActivityForResult(
                    intent1,
                    CROP_IMAGE_CODE
                )
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(
                    applicationContext,
                    "User cancelled image capture", Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                // failed to capture image
                Toast.makeText(
                    applicationContext,
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    fun launch(cls: Class<out CxBaseActivityKotlin>): GpLauncher {
        return GpLauncher(this, cls)
    }

    inner class GpLauncher(
        context: Context,
        cls: Class<out CxBaseActivityKotlin>
    ) {
        var requestCode = -1
        var pairs: ArrayList<Pair<View, String>>? =
            ArrayList()
        private val intent: Intent
        private var bundle: Bundle? = null
        private var finishThis = false
        private var clearStack = false
        fun addPair(view: View?, transition: String?): GpLauncher {
            if (pairs == null) pairs =
                ArrayList()
            pairs!!.add(Pair(view, transition))
            return this
        }

        fun launchActivity() {
            if (bundle != null) intent.putExtras(bundle!!)
            if (clearStack) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (pairs!!.size > 0) {
                val options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(gpBaseActivity!!, *pairs!!.toTypedArray())
                if (requestCode == -1) {
                    try {
                        ActivityCompat.startActivity(
                            gpBaseActivity!!,
                            intent, options.toBundle()
                        )
                    } catch (e: Exception) {
                        startActivity(intent)
                        gpBaseActivity!!.overridePendingTransition(
                            R.animator.scale_in,
                            R.animator.scale_out
                        )
                    }
                } else {
                    try {
                        ActivityCompat.startActivityForResult(
                            gpBaseActivity!!,
                            intent, requestCode, options.toBundle()
                        )
                    } catch (e: Exception) {
                        startActivityForResult(intent, requestCode)
                        gpBaseActivity!!.overridePendingTransition(
                            R.animator.scale_in,
                            R.animator.scale_out
                        )
                    }
                }
            } else {
                if (requestCode == -1) {
                    startActivity(intent)
                    //                    ActivityOptionsCompat options = ActivityOptionsCompat
//                            .makeCustomAnimation(gpBaseActivity, R.anim.slide_in_left, R.anim.zoom_out);
//                    ActivityCompat.startActivity(gpBaseActivity, intent, options.toBundle());
                } else startActivityForResult(intent, requestCode)
                gpBaseActivity!!.overridePendingTransition(
                    R.animator.scale_in,
                    R.animator.scale_out
                )
            }
            if (finishThis) finish()
        }

        fun getIntent(): Intent {
            if (bundle != null) intent.putExtras(bundle!!)
            return intent
        }

        fun finishThis(): GpLauncher {
            finishThis = true
            return this
        }

        fun setRequestCode(requestCode: Int): GpLauncher {
            this.requestCode = requestCode
            return this
        }

        fun clearStack(): GpLauncher {
            clearStack = true
            return this
        }

        fun addString(key: String?, value: String?): GpLauncher {
            if (bundle == null) bundle = Bundle()
            bundle!!.putString(key, value)
            return this
        }

        fun addBoolean(key: String?, value: Boolean): GpLauncher {
            if (bundle == null) bundle = Bundle()
            bundle!!.putBoolean(key, value)
            return this
        }

        fun addInt(key: String?, value: Int): GpLauncher {
            if (bundle == null) bundle = Bundle()
            bundle!!.putInt(key, value)
            return this
        }

        fun addLong(key: String?, value: Long): GpLauncher {
            if (bundle == null) bundle = Bundle()
            bundle!!.putLong(key, value)
            return this
        }

        fun addDouble(key: String?, value: Double): GpLauncher {
            if (bundle == null) bundle = Bundle()
            bundle!!.putDouble(key, value)
            return this
        }

        init {
            intent = Intent(context, cls)
        }
    }

    companion object {
        const val CROP_IMAGE_CODE = 300
        const val EVENT_PUBLISH = "theeventthatwaspublished"
        const val SELECT_CONTENT = 716
        const val PICK_PICTURE_GALLERY = 1919
        const val MY_PERMISSIONS_REQUEST_AUDIO = 101
        const val MY_PERMISSIONS_REQUEST_VIDEO = 102
        private const val MY_PERMISSIONS_REQUEST_IMAGE = 100
        private const val MY_PERMISSIONS_REQUEST_CAMERA = 120
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 200
        private const val MY_PERMISSIONS_REQUEST_CONTACTS = 201
        private const val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 130
        private const val IMAGE_DIRECTORY_NAME = "Camera"

        // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
        private const val TWITTER_KEY = "manish.saini@goparties.com"
        private const val TWITTER_SECRET = "saini@123"
        private fun getOutputMediaFile(type: Int): File? {

            // External sdcard location
            val mediaStorageDir = File(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME
            )

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(
                        IMAGE_DIRECTORY_NAME,
                        "Oops! Failed create "
                                + IMAGE_DIRECTORY_NAME + " directory"
                    )
                    return null
                }
            }
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val mediaFile: File
            mediaFile = if (type == FileColumns.MEDIA_TYPE_IMAGE) {
                File(
                    mediaStorageDir.path + File.separator
                            + "IMG_" + timeStamp + ".jpg"
                )
            } else {
                return null
            }
            return mediaFile
        }
    }
}