package mp.app.calonex.landlord.adapter

import android.content.Context
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import mp.app.calonex.R
import java.util.ArrayList

class FullImageAdapter(
    private val context: Context,
    private val imageModelArrayList: ArrayList<String>
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


        Log.e(
            "Loaded",
            "File Path ==> " + context.getString(R.string.base_url_img2) + imageModelArrayList[position]
        )

        Glide.with(context)
            .load(context.getString(R.string.base_url_img2) + imageModelArrayList[position])
            .placeholder(R.drawable.logo)
            .into(imageView!!)

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