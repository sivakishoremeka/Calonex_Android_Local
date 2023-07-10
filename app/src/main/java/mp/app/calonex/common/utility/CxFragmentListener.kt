package mp.app.calonex.common.utility

import android.view.View

interface CxFragmentListener {
    fun onCallback(
        tag: Any?,
        view: View?,
        result: Boolean
    )
}