package mp.app.calonex.common.utility

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class UsPhoneNumberFormatter(private val etMobile: TextInputEditText) : TextWatcher {
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        val text = etMobile.text.toString()
        val textlength = etMobile.text!!.length
        if (text.endsWith(" ")) return
        if (textlength == 1) {
            if (!text.contains("(")) {
                etMobile.setText(StringBuilder(text).insert(text.length - 1, "(").toString())
                etMobile.setSelection(etMobile.text!!.length)
            }
        } else if (textlength == 5) {
            if (!text.contains(")")) {
                etMobile.setText(StringBuilder(text).insert(text.length - 1, ")").toString())
                etMobile.setSelection(etMobile.text!!.length)
            }
        } else if (textlength == 6) {
            if (!text.contains(" ")) {
                etMobile.setText(StringBuilder(text).insert(text.length - 1, " ").toString())
                etMobile.setSelection(etMobile.text!!.length)
            }
        } else if (textlength == 10) {
            if (!text.contains("-")) {
                etMobile.setText(StringBuilder(text).insert(text.length - 1, "-").toString())
                etMobile.setSelection(etMobile.text!!.length)
            }
        }
    }

    override fun afterTextChanged(editable: Editable) {}
}