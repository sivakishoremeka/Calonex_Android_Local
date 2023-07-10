package mp.app.calonex.ldBkAddBank

import android.os.Bundle
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2


class ShowBankPersonalInfoActivity : CxBaseActivity2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_bank_account_details)

        initComponent()
        startHandler()
    }

    fun initComponent() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}