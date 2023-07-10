package mp.app.calonex.tenant.fragment

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import mp.app.calonex.R


class WebViewDialogFragment(context: Context) : Dialog(context) {

    fun customDialog(uri: Uri) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.web_view_dialog)
        dialog.setTitle("")
//        dialog.setCancelable(false)

        val webView = dialog.findViewById<View>(R.id.web_view) as WebView
//        val btn_dialog = dialog.findViewById<View>(R.id.btDialog) as Button

        webView.webViewClient = WebViewClient()
        webView.requestFocus()
        webView.getSettings().supportZoom()
        //webView.getSettings().setAppCacheEnabled(true)
        webView.getSettings().setDomStorageEnabled(true)
        webView.getSettings().setBuiltInZoomControls(true)
        webView.getSettings().setLoadWithOverviewMode(true)
        webView.getSettings().setUseWideViewPort(true)
        webView.getSettings().setPluginState(WebSettings.PluginState.ON)
        webView.getSettings().getPluginState()
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true

        val url = "https://docs.google.com/gview?embedded=true&url=" + uri
        webView.loadUrl(url)


//        btn_dialog.setOnClickListener(View.OnClickListener {
//            dialog.dismiss()
//        })

        dialog.show()

    }


}