package mp.app.calonex.tenant.fragment

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import mp.app.calonex.R


class FullImageDialogFragment(context: Context) : Dialog(context) {

    fun customDialog(uri: Uri) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.full_image_dialog)
        dialog.setTitle("")
        dialog.setCancelable(false)

        val imageView = dialog.findViewById<View>(R.id.imageView) as ImageView
        val btn_dialog = dialog.findViewById<View>(R.id.btDialog) as Button

        Glide.with(context)
            .load(uri)
            .into(imageView)

        btn_dialog.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        dialog.show()

    }


}