package mp.app.calonex.common.utility

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import mp.app.calonex.R
import java.util.*

abstract class CxBaseFragmentKotlin : AppCompatDialogFragment() {
    enum class MODE {
        HOME, SEARCH, NEAR, TRENDING, DEALS, MASTER_CAL, MY_PROFILE, MY_PARTIES
    }

    var currentMode: MODE? = null
        protected set
    var gpBaseActivity: CxBaseActivityKotlin? = null
    lateinit var cxFragmentListener: CxFragmentListener
    var views: View? = null


    val animationType: Int
        get() = R.style.DialogAnimationRight

    val isFullScreen: Boolean
        get() = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog
        if (isFullScreen) {
            dialog = Dialog(
                Objects.requireNonNull(activity)!!,
                R.style.AppTheme_NoActionBar
            )
            Objects.requireNonNull(dialog.window)
                ?.attributes?.windowAnimations = animationType
            dialog.window
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } else {
            dialog = super.onCreateDialog(savedInstanceState)
            Objects.requireNonNull(dialog.window)
                ?.attributes?.windowAnimations = animationType
        }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }


    fun setGpFragmentListener(gpFragmentListener: CxFragmentListener?) {
        this.cxFragmentListener = gpFragmentListener!!
    }

    /**
     * called when the fragment needs to update the UI
     */
    fun refreshUI() {}

    val noDataTitle: String
        get() = "Sorry, No Results Found!"

    // the method is required to overridden in the activities
    val noDataDescription: String
        get() = "We advise that to discover relevant results please change your location"

    val noDataButton: String
        get() = "Retry"

    val noNetworkTitle: String
        get() = "No Network"

    val noNetworkDescription: String
        get() = "Please make sure that you are connected to the internet.\n\nCheck your internet settings..!!"

    val noNetworkButton: String
        get() = "Retry"


    override fun onResume() {
        super.onResume()
    }

    private fun makeBundle() {
        var bundle = arguments
        if (bundle == null) {
            bundle = Bundle()
            arguments = bundle
        }
    }

    fun addBundleLong(
        key: String?,
        value: Long
    ): CxBaseFragmentKotlin {
        makeBundle()
        Objects.requireNonNull(arguments)?.putLong(key, value)
        return this
    }

    fun addBundleInt(
        key: String?,
        value: Int
    ): CxBaseFragmentKotlin {
        makeBundle()
        Objects.requireNonNull(arguments)?.putInt(key, value)
        return this
    }

    fun addBundleDouble(
        key: String?,
        value: Double
    ): CxBaseFragmentKotlin {
        makeBundle()
        Objects.requireNonNull(arguments)?.putDouble(key, value)
        return this
    }

    fun addBundleSting(
        key: String?,
        value: String?
    ): CxBaseFragmentKotlin {
        makeBundle()
        Objects.requireNonNull(arguments)?.putString(key, value)
        return this
    }

    fun addBundleBoolean(
        key: String?,
        value: Boolean
    ): CxBaseFragmentKotlin {
        makeBundle()
        Objects.requireNonNull(arguments)?.putBoolean(key, value)
        return this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        gpBaseActivity = activity as CxBaseActivityKotlin
    }

    abstract val layoutId: Int

    abstract fun onLayoutCreated()
    val title: String
        get() = ""

    fun showToast(`object`: Any?) {
        gpBaseActivity!!.showToast(`object`)
    }

    fun showToastLong(`object`: Any?) {
        gpBaseActivity!!.showToastLong(`object`)
    }

    fun showApologies() {
        gpBaseActivity!!.showApologies()
    }

    override fun onPause() {
        super.onPause()
    }

    protected val screenName: String
        protected get() = visibleFragment

    val isUnSubscribeOnStop: Boolean
        get() = true

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (views == null) {
            views = inflater.inflate(layoutId, null)
            onLayoutCreated()
        }
        return views
    }

    val visibleFragment: String
        get() = javaClass.simpleName

    override fun show(
        manager: FragmentManager,
        tag: String?
    ) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag).addToBackStack(null)
            ft.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            Log.e("IllegalStateException", "Exception", e)
        }
    }

    init {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    }
}