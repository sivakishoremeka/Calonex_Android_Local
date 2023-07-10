package mp.app.calonex.landlord.adapter

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import mp.app.calonex.R
import java.util.*

class FullImageTenantAdapter(
    private val context: Context,
    private val imageModelArrayList: ArrayList<Uri>
) : PagerAdapter() {
    private val inflater: LayoutInflater


    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return imageModelArrayList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.layout_image_slide, view, false)!!

        val imageView = imageLayout
            .findViewById(R.id.img_slide) as ImageView


        imageView.setImageURI(imageModelArrayList[position])

        view.addView(imageLayout, 0)


        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}