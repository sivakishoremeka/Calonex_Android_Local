package mp.app.calonex.tenant.activity

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.web_view_dialog.*
import mp.app.calonex.R

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val url = intent.getStringExtra("url")
        setContentView(R.layout.web_view_dialog)
        web_view.webViewClient = WebViewClient()
        web_view.settings.setSupportZoom(true)
        web_view.settings.javaScriptEnabled = true
        val url = "https://mindorks.s3.ap-south-1.amazonaws.com/courses/MindOrks_Android_Online_Professional_Course-Syllabus.pdf"
//        webView.loadUrl("https://uat-apigateway.calonex.com/api/root-service/api/s3Bucket/download?fileName=https://s3.us-east-1.amazonaws.com/calonex-bucket/Multi Tenant Apps - Mobile (1).pdf")
        web_view.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")

    }
}