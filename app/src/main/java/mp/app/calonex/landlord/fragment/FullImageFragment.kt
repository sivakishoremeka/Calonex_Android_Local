package mp.app.calonex.landlord.fragment

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.adapter.FullImageAdapter

class FullImageFragment(context: Context) : Dialog(context) {

    fun customDialog(imageDocList: ArrayList<String>) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.fragment_full_image)
        dialog.setTitle("Images")

        val vpFullImage: ViewPager = dialog.findViewById(R.id.vp_full_image)
        val ivFdClose: ImageView = dialog.findViewById(R.id.iv_fd_close)

        vpFullImage.adapter = FullImageAdapter(context, imageDocList)


        val index = Kotpref.propertyImageIndex
        vpFullImage.currentItem = index

        ivFdClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}