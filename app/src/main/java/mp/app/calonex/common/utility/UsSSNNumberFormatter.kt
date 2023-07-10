package mp.app.calonex.common.utility

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class UsSSNNumberFormatter(private val etSsn: TextInputEditText) : TextWatcher {
    var count=0
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        val text = etSsn.text.toString()
        val textlength = etSsn.text!!.length
        if (text.endsWith(" ")) return
        if (textlength == 4) {
            if (!text.contains("-")) {
                count=1
                etSsn.setText(StringBuilder(text).insert(text.length - 1, "-").toString())
                etSsn.setSelection(etSsn.text!!.length)
            }else{
                count=0
            }
        } else if (textlength == 7) {
            if (text.contains("-") && count==1) {
                count=2
                etSsn.setText(StringBuilder(text).insert(text.length - 1, "-").toString())
                etSsn.setSelection(etSsn.text!!.length)
            }else{
                count=1
            }
        }
    }

    override fun afterTextChanged(editable: Editable) {
//        var pattern = "^(?!000|666)[0-9]{3}([ -]?)(?!00)[0-9]{2}\\1(?!0000)[0-9]{4}\$"
//        var patt = Pattern.compile(pattern)
//        var result = editable.replace(patt, "")
    }
}