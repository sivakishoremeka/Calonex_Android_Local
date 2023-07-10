package mp.app.calonex.landlord.adapter

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.fragment.FullImageFragment
import java.util.ArrayList


class ImagePagerAdapter(private val context: Context, val activity: Activity, private val imageModelArrayList: ArrayList<String>) : PagerAdapter() {
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
        if(imageModelArrayList.size>0){
            Glide.with(context)
                .load(context.getString(R.string.base_url_img2)+imageModelArrayList[position])
                .placeholder(context.getDrawable(R.drawable.bg_default_property))
                .into(imageView!!)
            /*imageView.scaleType=ImageView.ScaleType.FIT_XY*/
            view.addView(imageLayout, 0)

            imageView.setOnClickListener {
                Kotpref.propertyImageIndex=position
                Kotpref.propertyImageIndex=0
                FullImageFragment(activity!!).customDialog(imageModelArrayList)


            }
        }else{
            imageView.scaleType=ImageView.ScaleType.FIT_XY
            imageView.setBackgroundResource(R.drawable.logo)
        }





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