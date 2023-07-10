package mp.app.calonex.landlord.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_upload_photo.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.mArrayUri
import mp.app.calonex.tenant.fragment.FullImageDialogFragment

class TenantVerifyImageAdapter(
    var context: Context,
    var list: ArrayList<Uri>,
    var imgFull: ImageView?,
    var txtTag: TextView?
) : RecyclerView.Adapter<TenantVerifyImageAdapter.ViewHolder>() {


    lateinit var tasks: ArrayList<Uri>
    lateinit var mItemClickListener: TenantVerifyImageAdapter.MyAdapterListener
    var selectPosition: Int = 0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var url: String = ""
        for (i in 0 until list[position].pathSegments.size) {
            if (i == 0) {
                url = url + list[position].pathSegments[i]
            } else if (i == 1) {
                url = url + "//" + list[position].pathSegments[i]
            } else {
                url = url + "/" + list[position].pathSegments[i]
            }
        }
        Glide.with(context)
            .load(context.getString(R.string.base_url_img) + url)
//            .load(context.getString(R.string.base_url_img) + list[position].path)
            .into(holder.imgPic)


        holder.imgPic.setImageURI(list[position])

        if (imgFull != null) {
            imgFull?.setImageURI(list[0])
        }
        holder.imgPic.setOnClickListener(View.OnClickListener {
            FullImageDialogFragment(context).customDialog(list[position])
        })
        holder.ibDel.setOnClickListener(View.OnClickListener {
            list.removeAt(position)
            mArrayUri.clear()
            mArrayUri = list

            if (position == selectPosition) {
                imgFull?.setImageURI(null)
            }
            if (list.size == 0) {
                if (txtTag != null) {
                    txtTag?.visibility = View.VISIBLE
                }
            }
            notifyDataSetChanged()
        })

    }

    fun setData(tasks: ArrayList<Uri>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPic = itemView.iv_photo!!
        val ibDel = itemView.ib_del!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_upload_photo, parent, false)
        return ViewHolder(v)
    }

    fun setOnItemClick(itemClickListener: MyAdapterListener): Unit {
        this.mItemClickListener = itemClickListener
    }

    interface MyAdapterListener {
        fun onItemViewClick(webUrl: String, position: Int)
    }
}